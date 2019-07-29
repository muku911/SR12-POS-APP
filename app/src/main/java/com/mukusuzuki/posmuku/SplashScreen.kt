package com.mukusuzuki.posmuku

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mukusuzuki.posmuku.data._Product
import com.mukusuzuki.posmuku.database.Gudang
import com.mukusuzuki.posmuku.database.Product
import com.mukusuzuki.posmuku.database.database
import kotlinx.android.synthetic.main.activity_splash_screen.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.toast
import java.util.*


class SplashScreen : AppCompatActivity() {
    private var productList: ArrayList<_Product> = arrayListOf()

    private var mDelayHandler: Handler? = null
    private var myDELAY: Long = 1543 //1s
    private var countData:Int = 0

    companion object{
        const val GOOGLE_SIGN:Int = 123
    }
    private lateinit var mAuth:FirebaseAuth
    private lateinit var mGoogleSignInClient:GoogleSignInClient

    @SuppressLint("SetTextI18n")
    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing){
            tv_Status.text = "Checking Database..."
            database.use {
                val result = select(Gudang.TABEL_NAME)
                val getResult = result.parseList(classParser<Gudang>())
                countData = getResult.size
            }

            if (countData == 0){
                tv_Status.text = "Creating Database..."
                dataForDatabase()
                var i = 0
                while (i < productList.size){
                    try {
                        database.use {
                            insert(Product.TABEL_NAME,
                                Product.ITEM_CODE to productList[i].item_code,
                                Product.ITEM_NAME to productList[i].item_name,
                                Product.ITEM_PRICE to productList[i].item_price)
                        }
                    } catch (e: SQLiteConstraintException){

                    }
                    try {
                        database.use {
                            insert(Gudang.TABEL_NAME, Gudang.ITEM_CODE to productList[i].item_code,
                                Gudang.ITEM_COUNT to 0)
                        }
                    } catch (e: SQLiteConstraintException){

                    }
                    i++
                }
                tv_Status.text = "Database Created!"
                database.use {
                    val result = select(Gudang.TABEL_NAME)
                    val getResult = result.parseList(classParser<Gudang>())
                    countData = getResult.size
                }
            }
            gotoLogin()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)


        mDelayHandler = Handler()
        mDelayHandler!!.postDelayed(mRunnable, myDELAY)

    }

    public override fun onDestroy() {

        if (mDelayHandler != null){
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }

    private fun dataForDatabase() {
        initToDatabase("AMC20", "Acne Moist Cream 20gr", 115000)
        initToDatabase("AMNC20", "Acne Moist & Night Cream 20gr", 230000)
        initToDatabase("ANC20", "Acne Night Cream 20gr", 115000)
        initToDatabase("AP30", "Acne Peel 30ml", 120000)
        initToDatabase("AS18", "Acne Serum 18ml", 100000)
        initToDatabase("ATL8", "Acne Totol 8gr", 55000)
        initToDatabase("ATR50", "Acne Toner 50ml", 95000)
        initToDatabase("ATT10", "Acne Treatment 10gr", 180000)
        initToDatabase("BDC20", "Brightening Night Cream 20gr", 115000)
        initToDatabase("BDN20", "Brightening Day Cream 20gr", 115000)
        initToDatabase("BDNC20", "Brightening Day & Night Cream 20gr", 230000)
        initToDatabase("BS20", "Brightening Serum 20ml", 140000)
        initToDatabase("BW250", "Body Wash	250ml", 55000)
        initToDatabase("CM100", "Cleansing Milk 100ml", 65000)
        initToDatabase("CS20", "Crasny Serum 20ml", 120000)
        initToDatabase("DS60", "Deodorant Spray 60ml", 40000)
        initToDatabase("ECP15", "Exclusive Compact Powder 15gr", 95000)
        initToDatabase("FWB100", "Facial Wash Bulus 100ml", 50000)
        initToDatabase("FWGT100", "Facial Wash Green Tea 100ml", 35000)
        initToDatabase("FWGT250", "Facial Wash Green Tea 250ml", 55000)
        initToDatabase("FWM100", "Facial Wash Madu 100ml", 40000)
        initToDatabase("FWM250", "Facial Wash Madu 250ml", 80000)
        initToDatabase("GS20", "Gold Serum 20ml", 140000)
        initToDatabase("HHS20", "Honey Herbal Soap 20gr", 22000)
        initToDatabase("HSC20", "Herbal Soap Coffee 20gr", 22000)
        initToDatabase("KDC24", "Krasny Day Cream 24gr", 75000)
        initToDatabase("KDNC24", "Krasny Day & Night Cream 24gr", 150000)
        initToDatabase("KMC100", "Kefir Mask Coffee 100gr", 125000)
        initToDatabase("KMC50", "Kefir Mask Coffee 50gr", 65000)
        initToDatabase("KME100", "Kefir Mask Etawa 100gr", 125000)
        initToDatabase("KME50", "Kefir Mask Etawa 50gr", 65000)
        initToDatabase("KMM100", "Kefir Mask Milk 100gr", 125000)
        initToDatabase("KMM50", "Kefir Mask Milk 50gr", 65000)
        initToDatabase("KNC24", "Krasny Night Cream 24gr", 75000)
        initToDatabase("LCS125", "Lulur Coffee Scrub 125gr", 40000)
        initToDatabase("LP8", "Lip Care 8gr", 25000)
        initToDatabase("LR100", "Lulur Rempah 100gr", 40000)
        initToDatabase("MC12", "Mask Coffee 12gr", 75000)
        initToDatabase("MJK60", "Manjakani 60btr", 70000)
        initToDatabase("MOI30", "Massage Oil Bulus 30ml", 95000)
        initToDatabase("MRS20", "Milky Rice Soap 20gr", 22000)
        initToDatabase("RHS20", "Rice Herbal Soap 20gr", 22000)
        initToDatabase("RS18", "Revitalizing Serum 18ml", 85000)
        initToDatabase("SB20", "Sabun Bulus 20gr", 25000)
        initToDatabase("SC12", "Sun Care 12gr", 53000)
        initToDatabase("SEDC20", "Spot Essent Day Cream 20gr", 130000)
        initToDatabase("SEDNC2", "Spot Essent Night Cream 20gr", 130000)
        initToDatabase("SEDNC20", "Spot Essent Day & Night Cream 20gr", 260000)
        initToDatabase("SS60", "Salimah Slim 60capsul", 55000)
        initToDatabase("TWC60", "Toner with Camomile 60ml", 60000)
        initToDatabase("VCOC100", "VCO Cair 100ml", 38000)
        initToDatabase("VCOC250", "VCO Cair 250ml", 78000)
        initToDatabase("VCOC60", "VCO Cair 60ml", 25000)
        initToDatabase("VCOT100", "VCO Capsul 100btr", 55000)
        initToDatabase("WBL100", "White Body Lotion 100ml", 46000)
    }

    private fun initToDatabase(code: String, name: String, price: Int) {
        val data = _Product(code, name, price)
        productList.add(data)
    }

    private fun gotoMain() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun gotoLogin(){
        progressBarS.visibility = View.GONE
        tv_Status.visibility = View.GONE
        btnGoogleLogin.visibility = View.VISIBLE
        tvCreator.text = "APPLICATION"
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //gotoMain()
    }

     fun signInGoogle(view: View){
        val signIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signIntent, GOOGLE_SIGN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIGN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("TAG", "Google sign in failed", e)
            }

        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        println("firebaseAuthWithGoogle: " +account.id)

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")
                    val user = mAuth.currentUser
                    val name = user?.displayName
                    val email = user?.email
                    val photo = user?.photoUrl
                    println("Nama : $name")
                    println("Email : $email")
                    println("URL : $photo")
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    toast("Authentication failed.")
                }
            }
    }

}
