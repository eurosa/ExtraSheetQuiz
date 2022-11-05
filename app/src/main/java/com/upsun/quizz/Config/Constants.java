package com.upsun.quizz.Config;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 04,April,2020
 */
public class Constants {
    public static final String APP_NAME="Quiz App";

    //Database Reference
    //users
    public static final String USER_REF="users";
    public static final String PROFILE_REF="profile images";
    //admin
    public static final String ADMIN_REF="admin";
    public static final String QUIZ_REF="quiz";
    public static final String ADD_QUESTION_REF="quiz";
    public static final String CATEGORY_REF="categories";
    public static final String QUESTION_REF="questions";
    public static final String CONFIG_REF="config";
    public static final String QUIZ_QUES_REF="quiz_ques";
    public static final String RANK_REF="ranks";
    public static final String INSTRUCTION_REF="instructions";
    public static final String JOINED_QUIZ_REF="joined_quiz";
    public static final String QUIZ_RESULTS_REF="quiz_results";
    public static final String RANK_REWARD_REF="quiz_rank_reward";//quiz_rank_reward
    public static final String POINT_REF="points";
    public static final String PMETHOD_REF="payment_methods";
    public static final String TRANS_REF="transactions";
    public static final String WITHDRAW_REF="withdraw_request";
    public static final String UPDATER_REF="app_updater";
    public static final String CONTACT_REF="contact_us";
    public static final String SECTION_REF="sections";
    public static final String HISTORY_REF="history";
    public static final String SEC_QUES_REF="sec_questions";
    public static final String PRO_REF="products";

    //Session Keys
    public static final String PREF_NAME="quiz_app";
    public static final String KEY_ID="id";
    public static final String KEY_NAME="name";
    public static final String KEY_MOBILE="mobile";
    public static final String KEY_EMAIL="email";
    public static final String KEY_USER_TYPE="user_type";
    public static final String KEY_PROFILE_IMG="profile_img";
    public static final String KEY_WALLET="wallet";
    public static final String KEY_REWARDS="rewards";
    public static final String KEY_QUIZ_STATUS="quiz_status";
    public static final String IS_LOGIN="is_login";
    public static final String KEY_DEVICE_ID="device_id";
    public static final String referredBy="referredBy";
    public static final String IS_APP_FIRST_TIME_LAUNCH="is_app_first_time_launch";
    public static final String EXPIRED_TIME="ExpiredTime";

    public static final String CAT_ID="cat_id";

    //Broadcast Receiver
    public static final String BROAD_WALLET="wallet_broadcast";
    public static final String BROAD_REWARDS="rewards_broadcast";

    //payment gateway
    public static final long API_CONNECTION_TIMEOUT = 1201;
    public static final long API_READ_TIMEOUT = 901;

    public static final String BASE_URL ="https://mobileservices.anshuwap.com/";
    public static final String SERVER_main_folder = "payu/";
    public static int REDUCTION_WALLET_AMOUNT = 0;
    public static int REDUCTION_REWARDS_AMOUNT = 0;
    public static int WITHDRAW_LIMIT_AMOUNT = 0;

    public static String ad_app_id="ca-app-pub-9030161066631347~5744614565";
    public static String ad_banner="ca-app-pub-9030161066631347/6807248946";
    public static String ad_intertitial="ca-app-pub-9030161066631347/1554922267";



}
