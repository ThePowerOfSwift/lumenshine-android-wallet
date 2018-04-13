package com.soneso.stellargate.model.user

import com.soneso.stellargate.domain.data.Account
import com.soneso.stellargate.domain.data.UserLogin
import com.soneso.stellargate.model.dto.DataProvider
import com.soneso.stellargate.model.dto.DataStatus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.bouncycastle.util.encoders.Base64

/**
 * Class used to user operations to server.
 * Created by cristi.paval on 3/26/18.
 */
class UserRepository(private val userApi: UserApi, private val userDao: UserDao) : UserDao by userDao {

    fun createUserAccount(account: Account): DataProvider<Account> {
        val dataProvider = DataProvider<Account>()
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
                .doOnSubscribe { dataProvider.liveStatus.value = DataStatus.LOADING }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    dataProvider.data = account
                    dataProvider.liveStatus.value = DataStatus.SUCCESS
                }, {
                    dataProvider.errorMessage = "Error at registration!"
                    dataProvider.liveStatus.value = DataStatus.ERROR
                })
        userDao.saveUserLogin(UserLogin(account.email, "", ""))
        return dataProvider
    }
}