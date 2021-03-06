/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.03.20 16:20
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di.modules

import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.conversations.repository.ConversationsRepository
import com.mmdev.business.local.LocalUserRepository
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.business.places.repository.PlacesRepository
import com.mmdev.business.remote.RemoteUserRepository
import com.mmdev.data.auth.AuthRepositoryImpl
import com.mmdev.data.cards.CardsRepositoryImpl
import com.mmdev.data.chat.ChatRepositoryImpl
import com.mmdev.data.conversations.ConversationsRepositoryImpl
import com.mmdev.data.pairs.PairsRepositoryImpl
import com.mmdev.data.places.PlacesRepositoryImpl
import com.mmdev.data.user.UserRepositoryLocal
import com.mmdev.data.user.UserRepositoryRemoteImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

	@Provides
	@Singleton
	fun authRepository(repository: AuthRepositoryImpl): AuthRepository = repository

	@Provides
	@Singleton
	fun cardsRepository(repository: CardsRepositoryImpl): CardsRepository = repository

	@Provides
	@Singleton
	fun chatRepository(repository: ChatRepositoryImpl): ChatRepository = repository

	@Provides
	@Singleton
	fun conversationsRepository(repository: ConversationsRepositoryImpl): ConversationsRepository = repository

	@Provides
	@Singleton
	fun pairsRepository(repository: PairsRepositoryImpl): PairsRepository = repository

	@Provides
	@Singleton
	fun placesRepository(repository: PlacesRepositoryImpl): PlacesRepository = repository

	@Provides
	@Singleton
	fun localUserRepository(repository: UserRepositoryLocal): LocalUserRepository = repository

	@Provides
	@Singleton
	fun remoteUserRepository(repository: UserRepositoryRemoteImpl): RemoteUserRepository = repository

}
