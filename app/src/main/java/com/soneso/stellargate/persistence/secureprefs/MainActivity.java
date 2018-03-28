//
///*
//   _____                       _        _    _
//  / ____|                     | |      | |  | |
// | (___   __ _ _ __ ___  _ __ | | ___  | |  | |___  __ _  __ _  ___
//  \___ \ / _` | '_ ` _ \| '_ \| |/ _ \ | |  | / __|/ _` |/ _` |/ _ \
//  ____) | (_| | | | | | | |_) | |  __/ | |__| \__ \ (_| | (_| |  __/
// |_____/ \__,_|_| |_| |_| .__/|_|\___|  \____/|___/\__,_|\__, |\___|
//                        | |                               __/ |
//                        |_|                              |___/
// */
//public class MainActivity extends AppCompatActivity {
//
//    private static final String TAG = MainActivity.class.getSimpleName();
//    private static final String SAMPLE_ALIAS = "MYALIAS";
//
//    @BindView (R.id.toolbar)
//    Toolbar toolbar;
//
//    @BindView (R.id.ed_text_to_encrypt)
//    EditText edTextToEncrypt;
//
//    @BindView (R.id.tv_encrypted_text)
//    TextView tvEncryptedText;
//
//    @BindView (R.id.tv_decrypted_text)
//    TextView tvDecryptedText;
//
//    private EnCryptor encryptor;
//    private DeCryptor decryptor;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);
//        setSupportActionBar(toolbar);
//
//        encryptor = new EnCryptor();
//
//        try {
//            decryptor = new DeCryptor();
//        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
//                IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @OnClick ({R.id.btn_encrypt, R.id.btn_decrypt})
//    public void onClick(final View view) {
//
//        final int id = view.getId();
//
//        switch (id) {
//            case R.id.btn_encrypt:
//                encryptText();
//                break;
//            case R.id.btn_decrypt:
//                decryptText();
//                break;
//        }
//    }
//
//    private void decryptText() {
//        try {
//            tvDecryptedText.setText(decryptor
//                    .decryptData(SAMPLE_ALIAS, encryptor.getEncryption(), encryptor.getIv()));
//        } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
//                KeyStoreException | NoSuchPaddingException | NoSuchProviderException |
//                IOException | InvalidKeyException e) {
//            Log.e(TAG, "decryptData() called with: " + e.getMessage(), e);
//        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void encryptText() {
//
//        try {
//            final byte[] encryptedText = encryptor
//                    .encryptText(SAMPLE_ALIAS, edTextToEncrypt.getText().toString());
//            tvEncryptedText.setText(Base64.encodeToString(encryptedText, Base64.DEFAULT));
//        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
//                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException e) {
//            Log.e(TAG, "onClick() called with: " + e.getMessage(), e);
//        } catch (InvalidAlgorithmParameterException | SignatureException |
//                IllegalBlockSizeException | BadPaddingException e) {
//            e.printStackTrace();
//        }
//    }
//}
