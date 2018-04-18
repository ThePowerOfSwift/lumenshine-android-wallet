package com.soneso.stellargate.networking

import com.soneso.stellargate.domain.data.Account
import com.soneso.stellargate.model.dto.ResponseObserver
import com.soneso.stellargate.model.dto.auth.RegistrationResponse
import com.soneso.stellargate.model.dto.auth.TfaRegistrationResponse
import com.soneso.stellargate.model.user.UserApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.bouncycastle.util.encoders.Base64

class UserRequester(private val userApi: UserApi) {

    fun registerUser(account: Account, responseObserver: ResponseObserver<RegistrationResponse>) {
        userApi.registerUser(
                account.email,
                Base64.toBase64String(account.passwordSalt),
                Base64.toBase64String(account.encryptedMasterKey),
                Base64.toBase64String(account.masterKeyIV),
                Base64.toBase64String(account.encryptedMnemonic),
                Base64.toBase64String(account.mnemonicIV),
                account.publicKeyIndex0,
                account.publicKeyIndex188
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(responseObserver)
    }

    fun confirmTfaRegistration(jwtToken: String, tfaCode: String, responseObserver: ResponseObserver<TfaRegistrationResponse>) {
        userApi.confirmTfaRegistration(jwtToken, tfaCode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(responseObserver)
    }
}