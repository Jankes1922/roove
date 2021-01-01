/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.data.repository.pairs

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.business.user.UserItem
import com.mmdev.data.core.BaseRepositoryImpl
import com.mmdev.data.core.MySchedulers
import com.mmdev.data.core.firebase.executeAndDeserializeSingle
import com.mmdev.data.repository.user.UserWrapper
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import io.reactivex.rxjava3.internal.operators.single.SingleCreate
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class PairsRepositoryImpl @Inject constructor(
	private val fs: FirebaseFirestore,
	userWrapper: UserWrapper
): PairsRepository, BaseRepositoryImpl(fs, userWrapper) {
	
	companion object {
		private const val USERS_COLLECTION = "users"
	}
	
	private fun matchesQuery(user: UserItem, cursorPosition: Int): Query = fs.collection(USERS_COLLECTION)
		.document(user.baseUserInfo.userId)
		.collection(USER_MATCHED_COLLECTION_REFERENCE)
		.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
		.orderBy(MATCHED_DATE_FIELD, Query.Direction.DESCENDING)
		.limit(20)
		.startAfter(cursorPosition)
	
	private var cursorPosition = 0
	
	override fun getPairs(user: UserItem, cursorPosition: Int) =
		matchesQuery(currentUser, cursorPosition)
			.executeAndDeserializeSingle(MatchedUserItem::class.java)


	override fun getMatchedUsersList(): Single<List<MatchedUserItem>> =
		SingleCreate<List<MatchedUserItem>> { emitter ->
			reInit()
			matchesQuery(currentUser, 0)
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val matchesList = mutableListOf<MatchedUserItem>()
						for (doc in it) {
							matchesList.add(doc.toObject(MatchedUserItem::class.java))
						}
						emitter.onSuccess(matchesList)
						//reset cursor
						cursorPosition = 0
						cursorPosition += it.documents.size - 1
						
					}
					else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(MySchedulers.io())

	override fun getMoreMatchedUsersList(): Single<List<MatchedUserItem>> =
		Single.create(SingleOnSubscribe<List<MatchedUserItem>> { emitter ->
			matchesQuery(currentUser, cursorPosition)
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val paginateMatchesList = ArrayList<MatchedUserItem>()
						for (doc in it) {
							paginateMatchesList.add(doc.toObject(MatchedUserItem::class.java))
						}
						emitter.onSuccess(paginateMatchesList)
						
						cursorPosition += it.documents.size - 1
					}
					else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(MySchedulers.io())
}