package com.egco.storefinderproject.constant;

public class ApplicationConstant {
	public static final String SERVICE_URL = "http://egco-storefinder.meximas.com/";
	
	public static final String IMAGE_PATH_PREFIX = "http://egco-storefinder.meximas.com/productImageSrc/";

	public static final String SERVICE_ADD_CHECK_USER = "db_addandcheckuser.php";
	public static final String SERVICE_GET_STORE_DETAIL = "db_getstoredetail.php";
	public static final String SERVICE_UPDATE_STORE_DETAIL = "db_updatestoredetail.php";
	public static final String SERVICE_GET_USER_STATUS = "db_getuserstatus.php";
	public static final String SERVICE_GET_ALL_PRODUCT_LIST = "db_getallproductlist.php";
	public static final String SERVICE_ADD_NEW_PRODUCT = "productImageSrc/db_addproduct.php";

	public static final String PAGE_PREVIOUS = "_previousPAGE_";

	public static final String PAGE_BAN_PAGE = "_banPAGE_";
	public static final String PAGE_LOGIN_PAGE = "_loginPAGE_";
	public static final String PAGE_MAIN_PAGE = "_mainPAGE_";
	public static final String PAGE_RESULT_PAGE = "_resultPAGE_";
	public static final String PAGE_STORE_DETAIL_PAGE = "_storedetailPAGE_";
	public static final String PAGE_STORE_PAGE = "_storePAGE_";
	public static final String PAGE_PRODUCT_DETAIL_PAGE ="_productdetailPAGE_";
	
	public static final String ACTION_PRODUCT_DETAIL_ADD = "_add_productDetailPAGE__";
	public static final String ACTION_PRODUCT_DETAIL_EDIT = "_edit_productDetailPAGE__";
	
	public static final String INTENT_USER_STATUS = "_userSTATUS_";
	public static final String INTENT_DENIED_SERVICE_MESSAGE = "_deniedMessage_";
	public static final String INTENT_ACTION = "_action__";
	public static final String INTENT_CROPPED_IMAGE = "_croppedImage_";
	public static final String INTENT_PRODUCT_MODEL = "_productModel_";
	
	public static final int REQUEST_CODE_PHOTO_PICKER = 100;
}
