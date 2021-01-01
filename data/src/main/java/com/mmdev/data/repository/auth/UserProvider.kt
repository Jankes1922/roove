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

package com.mmdev.data.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.user.UserItem
import com.mmdev.data.AuthCollector
import com.mmdev.data.core.firebase.getAndDeserializeAsSingle
import com.mmdev.data.core.log.logError
import com.mmdev.data.core.log.logInfo
import io.reactivex.rxjava3.core.Observable

/**
 *
 */

class UserProvider(
	auth: FirebaseAuth,
	private val fs: FirebaseFirestore
) {
	
	companion object {
		private const val USERS_COLLECTION = "users"
		private const val TAG = "mylogs_UserProvider"
	}
	
	private val authObservable = AuthCollector(auth).firebaseAuthObservable.map {
		it.currentUser
	}
	
	fun getUser() = authObservable.switchMap { firebaseUser ->
			logInfo(TAG, "Collecting auth information...")
			
			if (firebaseUser != null) {
				
				logInfo(TAG, "Auth info exists...")
				
				getUserFromRemoteStorage(firebaseUser)
			}
			//not signed in
			else {
				logError(TAG, "Auth info does not exists...")
				
				null
			}
			
		}
	
	
	private fun getUserFromRemoteStorage(firebaseUser: FirebaseUser): Observable<UserItem?> =
		fs.collection(USERS_COLLECTION)
			.document(firebaseUser.email!!)
			.getAndDeserializeAsSingle(UserItem::class.java)
			.toObservable()
		
	
}