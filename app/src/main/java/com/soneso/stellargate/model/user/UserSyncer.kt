package com.soneso.stellargate.model.user

import com.soneso.stellargate.domain.data.User
import com.soneso.stellargate.model.dto.DataProvider
import com.soneso.stellargate.model.dto.DataStatus
import com.soneso.stellargate.model.dto.RegistrationRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Class used to user operations to server.
 * Created by cristi.paval on 3/26/18.
 */
class UserSyncer(private val userApi: com.soneso.stellargate.model.user.UserApi) : com.soneso.stellargate.model.user.UserRepository {

    override fun createUserAccount(user: User): DataProvider<User> {
        val registrationRequest = RegistrationRequest(
                user.email,
                user.securityData.publicKeyIndex0,
                user.securityData.publicKeyIndex188,
                user.securityData.derivedPassword,
                user.securityData.encryptedMasterKey,
                user.securityData.masterKeyIV,
                user.securityData.encryptedMnemonic,
                user.securityData.mnemonicIV
        )
        val dataProvider = DataProvider<User>()
        userApi.registerUser(registrationRequest)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe { dataProvider.liveStatus.value = DataStatus.LOADING }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    dataProvider.data = user
                    dataProvider.liveStatus.value = DataStatus.SUCCESS
                }, {
                    dataProvider.errorMessage = "Error at registration!"
                    dataProvider.liveStatus.value = DataStatus.ERROR
                })
        return dataProvider
    }
}