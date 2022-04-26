package com.pporte;

import java.io.FileReader;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.LoggerFactory;

import com.pporte.NeoBankEnvironment;
import com.pporte.utilities.Utilities;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import framework.v8.security.EncryptionHandler;

public class NeoBankEnvironment {
	private static String className = NeoBankEnvironment.class.getSimpleName();
	private static JSONObject JSON_LOCALE = null;
    private static  String KEYVALUE = null;
    private static  String FILE_UPLOAD_PATH = null; 
  	private static  String FILE_DOWNLOAD_PATH = null; 
  	private static  String LOGBACK_CONFIG_FILE_PATH = null; 
    private static final String CURRENCY_USD = "USD"; 
    private static final String TOKEN_REGISTRATION_AMOUNT = "1"; 
    private static final String BITCOIN_CODE =  "BTC"; 
    private static final String ETHEREUM_CODE = "ETH"; 
    private static final String LITECOIN_CODE = "LTC";     
    private static final String PORTE_TOKEN_INITIAL_BALANCE = "-0.5"; 
    private static final String STELLARLUMENS = "XLM"; 
    private static final String PORTE_TOKEN_CODE = "PORTE"; 
    private static final String VESSEL_COIN_CODE = "VESL"; 
    private static final String STELLAR_BITCOIN_CODE = "BTCX"; 

    private static final String USDC_CODE = "USDC"; 
    private static final String DEFAULT_CUSTMOMER_USERTYPE = "C0";
    private static final String DEFAULT_MERCHANT_USERTYPE = "M0";
    private static final String STELLAR_CUSTOMER_TRANSACTION_LIMIT = "15";
    private static final String STELLAR_TDA_TRANSACTION_LIMIT = "200";

    private static String OS = System.getProperty("os.name").toLowerCase();    
    private static boolean debugOn = false;									    
    private static  Logger logger = null;

    public static  String getUSDCurrencyId() throws Exception { return CURRENCY_USD; }
    public static  String getXLMCode() throws Exception { return STELLARLUMENS; }
    public static  String getVesselCoinCode() throws Exception { return VESSEL_COIN_CODE; }
    public static  String getPorteTokenCode() throws Exception { return PORTE_TOKEN_CODE; }
    public static  String getUSDCCode() throws Exception { return USDC_CODE; }
    public static  String getStellarBTCxCode() throws Exception { return STELLAR_BITCOIN_CODE; }
    public static  String getDefaultCustomerUserType() throws Exception { return DEFAULT_CUSTMOMER_USERTYPE; }
    public static  String getDefaultMerchantUserType() throws Exception { return DEFAULT_MERCHANT_USERTYPE; }
    public static String getInitialWalletBalanceOfPorteUtilityCoin() throws Exception{	return PORTE_TOKEN_INITIAL_BALANCE;}
    public static  String getBitcoinCode() throws Exception { return BITCOIN_CODE; }
	public static  String getEthereumCode() throws Exception { return ETHEREUM_CODE; }
	public static  String geLitecoinCode() throws Exception { return LITECOIN_CODE; }

	public static  String getFileUploadPath() throws Exception{ 			return FILE_UPLOAD_PATH; }													
	public static  String getFileDownloadPath() throws Exception{ 			return FILE_DOWNLOAD_PATH; }
	public static  String getDBUser() throws Exception{ 					return getParameters("DBUSER"); }														
	public static  String getDBPwd() throws Exception{ 					    return getParameters("DBPWD"); }
	public static  String getMYSQLDriver() throws Exception{ 				return getParameters("MYSQL_DRIVER"); }						
	public static  String getPostGreSQLDriver() throws Exception{ 			return getParameters("POSTGRESQL_DRIVER"); }
	public static  String getKeyValue() throws Exception{					 	return KEYVALUE; }															
	public static  String getDBURL() throws Exception{ 					    return getParameters("DB_URL"); }
	public static  String getLocalDateFormat() throws Exception{ 			return getParameters("LOCAL_DATEFORMAT"); }	
	//Blockchain
	public static  String getBlockChainInsert() throws Exception{ 			return getParameters("BLOCKCHAIN_INSERT"); }
	public static  String getBlockChainView() throws Exception{ 			return getParameters("BLOCKCHAIN_VIEW"); }
	public static  String getWalletLedgerChainMultiChainUser() throws Exception{ 			return getParameters("WALLETLEDGERCHAIN_MULTICHAINUSER"); }
	public static  String getCardVaultChainMultiChainUser() throws Exception{ 			return getParameters("CARDVAULTCHAIN_MULTICHAINUSER"); }
	public static  String getWalletLedgerChainRPCAuthKey() throws Exception{ 				return getParameters("WALLETLEDGERCHAIN_MUTIRPCKEY"); }
	public static  String getCardVaultChainRPCAuthKey() throws Exception{ 				return getParameters("CARDVAULTCHAIN_MUTIRPCKEY"); }
	public static  String getMultiChainWalletLedgerChainRPCURLPORT() throws Exception{ 		return getParameters("WALLETLEDGERCHAIN_MULTIURLPORT"); }	
	public static  String getMultiChainCardVaultChainRPCURLPORT() throws Exception{ 		return getParameters("CARDVAULTCHAIN_MULTIURLPORT"); }	
	public static  String getMultiChainWalletLedgerChainRPCIP() throws Exception{ 			return getParameters("WALLETLEDGERCHAIN_MULTIIP"); }	
	public static  String getMultiChainCardVaultChainRPCIP() throws Exception{ 			return getParameters("CARDVAULTCHAIN_MULTIIP"); }	
	public static  String getMultiChainWalletLedgerChainRPCPort() throws Exception{ 			return getParameters("WALLETLEDGERCHAIN_MULTIPORT"); }
	public static  String getMultiChainCardVaultChainRPCPort() throws Exception{ 			return getParameters("CARDVAULTCHAIN_MULTIPORT"); }
	public static  String getWalletLedgerBlockChainName() throws Exception{ 			return getParameters("WALLET_LEDGER_CHAIN_NAME"); }
	public static  String getCardVaultBlockChainName() throws Exception{ 			return getParameters("CARD_VAULT_CHAIN_NAME"); }
	public static String getBlockChainCardVaultStreamName() throws Exception{ 				return getParameters("CARD_VAULT_STREAM_NAME"); }
	public static String getBlockChainCustomerWalletStreamName() throws Exception{		return getParameters("CUSTOMER_WALLET_STREAM_NAME"); }
	public static String getBlockChainMerchantWalletStreamName() throws Exception{		return getParameters("MERCHANT_WALLET_STREAM_NAME"); }

    public  static  String getServletPath() throws Exception{     			return getParameters("SERVLET_PATH"); }	
    public  static  String getMutipartServletPath() throws Exception{     	return getParameters("MULTIPARTSERVLET_PATH"); }
    public  static  String getJSONServletPath() throws Exception{     		return getParameters("JSON_SERVLET_PATH"); }
	public static  String getAPIKeyPublic() throws Exception{ 				return getParameters("APIKEYPUB"); }	
	public static  String getAPIKeyPrivate() throws Exception{ 				return getParameters("APIKEYPVT"); }
	public static  String getFDocServerURL() throws Exception{ 				return getParameters("DOCUMENT_MANAGER_SERVER_URL"); }
    
    public static  String getErrorPage() throws Exception{     				return getParameters("ERROR_PAGE"); }
	public static  String getLoginPage() throws Exception{     				return getParameters("LOGIN_PAGE"); }
	public static String getUserDashboardPage() throws Exception {			return getParameters("DASHBOARD_PAGE");	}
	public static String getManageAssetPage() throws Exception{				return getParameters("MANAGE_ASSET_PAGE");	}
	public static String getDocUploadPage() throws Exception{				return getParameters("DOC_UPLOAD_PAGE");	}
	public static String getListAllDocPage() throws Exception{				return getParameters("DOC_ALLDOC_PAGE");	}
	public static String getManageUsersPage() throws Exception {			return getParameters("MANAGE_USERS_PAGE");	}
	public static String getUserProfilePage() throws Exception{				return getParameters("PROFILE_PAGE");	}
	public static String getOpsNewUsersPage() throws Exception{				return getParameters("NEWUSERS_PAGE"); }
	public static String getMerchantRegistrationPage() throws Exception{				return getParameters("MERCHANT_REGISTRATION_PAGE"); }
	public static String getCustomerRegistrationPage() throws Exception{				return getParameters("CUSTOMER_REGISTRATION_PAGE"); }
	
	// Customer Pages
	public static String getCustomerDashboadPage() throws Exception{				return getParameters("CUSTOMER_DASHBOARD_PAGE"); }
	public static String getCustomerProfilePage() throws Exception{				return getParameters("CUSTOMER_PROFILE_PAGE"); }
	public static String getCustomerViewWalletPage() throws Exception{				return getParameters("CUSTOMER_VIEW_WALLET_PAGE"); }
	public static String getCustomerCreateWalletPage() throws Exception{				return getParameters("CUSTOMER_CREATE_WALLET_PAGE"); }
	public static String getCustomerTopupWalletPage() throws Exception{				return getParameters("CUSTOMER_TOPUP_WALLET_PAGE"); }
	public static String getCustomerRegisterCardPage() throws Exception{				return getParameters("CUSTOMER_REGISTER_CARD_PAGE"); }
	public static String getCustomerShowAllCards() throws Exception{				return getParameters("CUSTOMER_SHOW_ALL_CARDS_PAGE"); }
	public static String getCustomerViewDisputePage() throws Exception{				return getParameters("CUSTOMER_VIEW_DISPUTE_PAGE"); }
	public static String getCustomerViewSpecificDisputePage() throws Exception{				return getParameters("CUSTOMER_VIEW_SPECIFIC_DISPUTE_PAGE"); }
	public static String getCustomerRaiseDisputePage() throws Exception{				return getParameters("CUSTOMER_RAISE_DISPUTE_PAGE"); }
	public static String getCustomerWalletPayAnyonePage() throws Exception{				return getParameters("CUSTOMER_PAY_ANYONE_PAGE"); }
	public static String getCustomerWalletCashTransactionsPage() throws Exception{				return getParameters("CUSTOMER_CASH_TRASACTION_PAGE"); }
	public static String getCustomerShpwtPorteCoinsPage() throws Exception{				return getParameters("CUSTOMER_SHOW_PORTE_COINS_PAGE"); }
	public static String getCustomerPorteBuyCoinsPage() throws Exception{				return getParameters("CUSTOMER_BUY_PORTE_COINS_PAGE"); }
	public static String getCustomerPorteDisplayTransactions() throws Exception{				return getParameters("CUSTOMER_PORTE_DISPLAY_TRANSACTION_PAGE"); }
	public static String getCustomerPorteTransferCoin() throws Exception{				return getParameters("CUSTOMER_PORTE_TRANSFER_COIN_PAGE"); }
	public static String getCustomerSellPorteCoin() throws Exception{				return getParameters("CUSTOMER_SELL_PORTE_COIN_PAGE"); }
	public static String getCustomerCryptoRegCoinPage() throws Exception{				return getParameters("CUSTOMER_CTYPTO_REG_COIN_PAGE"); }
	public static String getCustomerCryptoViewCoinPage() throws Exception{				return getParameters("CUSTOMER_CTYPTO_VIEW_COIN_PAGE"); }
	public static String getCustomerCryptoBuyCoinPage() throws Exception{				return getParameters("CUSTOMER_CTYPTO_BUY_COIN_PAGE"); }
	public static String getCustomerCryptoSellCoinPage() throws Exception{				return getParameters("CUSTOMER_CTYPTO_SELL_COIN_PAGE"); }
	public static String getCustomerCryptoPayAnyonePage() throws Exception{				return getParameters("CUSTOMER_CTYPTO_PAY_ANYONE_PAGE"); }
	public static String getCustomerCryptoViewTransPage() throws Exception{				return getParameters("CUSTOMER_CTYPTO_VIEW_TRANS_PAGE"); }
	public static String getCustomerLoyaltyViewPage() throws Exception{				return getParameters("CUSTOMER_LOYALTY_VIEW_PAGE"); }
	public static String getCustomerRegisterReceiver() throws Exception{				return getParameters("CUSTOMER_REGISTER_RECEIVER_PAGE"); }
	public static String getCustomerBuyPendingTransactionPage() throws Exception{				return getParameters("CUSTOMER_PENDING_TRANSACTION_PAGE"); }
	public static String getCustomerRegisterStellarAccountPage() throws Exception{				return getParameters("CUSTOMER_REGISTER_STELLAR_ACCOUNT_PAGE"); }
	public static String getCustomerRegisterDigitalCurrencyPage() throws Exception{				return getParameters("CUSTOMER_REGISTER_DIGITAL_CURRENCY_PAGE"); }
	public static String getCustomerExchangeDigitalCurrencyPage() throws Exception{				return getParameters("CUSTOMER_EXCHANGE_DIGITAL_CURRENCY_PAGE"); }
	public static String getCustomerLoyaltyRedeemPage() throws Exception{				return getParameters("CUSTOMER_LOYALTY_REDEEM_PAGE"); }
	public static String getCustomerPendingCurrencyTradingPage() throws Exception{				return getParameters("CUSTOMER_PENDING_CURRENCY_TRADING_PAGE"); }
	public static String getCustomerBuyNewPlanPage() throws Exception{	return getParameters("CUSTOMER_BUY_NEW_PLAN_PAGE");	}
	public static String getCustomerConfirmBuyPlanPage() throws Exception {	return getParameters("CUSTOMER_CONFIRM_BUY_PLAN_PAGE");		}
	public static String getOffRampPorteCoinPage() throws Exception{				return getParameters("CUSTOMER_OFFRAMP_PORTE_COIN_PAGE"); }
	public static String getCustomerViewClaimableBalancesPage() throws Exception{				return getParameters("CUSTOMER_VIEW_CLAIMABLE_BALANCE_PAGE"); }
	public static String getCustomerBuyBTCxPage() throws Exception{				return getParameters("CUSTOMER_BUY_BTCX_PAGE"); }
	public static String getCustomerViewBTCxRequest() throws Exception{				return getParameters("CUSTOMER_VIEW_PENDING_BTCX_PAGE"); }
	public static String getCustomerViewBTCxSwapRequest() throws Exception{				return getParameters("CUSTOMER_VIEW_PENDING_BTCX_EXCHANGE_PAGE"); }
	public static String getCustomerExchangeBTCxPage() throws Exception{				return getParameters("CUSTOMER_EXCHANGE_BTCX_PAGE"); }
	public static String getCustomerSetPasswordPage() throws Exception{				return getParameters("CUSTOMER_SET_PASSWORD_PAGE"); }
	public static String getBitcoinP2PTransferPage() throws Exception{				return getParameters("CUSTOMER_P2P_TRANSFER_BITCOIN"); }
	public static String getCustomerCreateBitcoinAcPage() throws Exception{				return getParameters("CUSTOMER_CREATE_BITCOIN_ACCOUNT"); }
	public static String getCustomerBuyBTCPage() throws Exception{				return getParameters("CUSTOMER_BUY_BTC_PAGE"); }
	public static String getCustomerViewBTCRequest() throws Exception{				return getParameters("CUSTOMER_VIEW_PENDING_BTC_PAGE"); }
	public static String getCustomerTransferBitCoinPage() throws Exception{				return getParameters("CUSTOMER_TRANSFER_BITCOIN_PAGE"); }
	public static String getCustomerViewBTCTransactionsPage() throws Exception{				return getParameters("CUSTOMER_VIEW_BTC_TXN_PAGE"); }
	public static String getCustomerSwapBTCToBTCxPage() throws Exception{				return getParameters("CUSTOMER_SWAP_BTC_TO_BTCX_PAGE"); }
	public static String getCustomerViewBTCToBTCxSwapTxnPage() throws Exception{				return getParameters("CUSTOMER_VIEW_BTC_TO_BTCX_SWAP_PAGE"); }
	public static String getCustomerBitcoinPage() throws Exception{				return getParameters("CUSTOMER_BITCOIN_PAGE"); }
	public static String getCustomerFiatRemittancePage() throws Exception{				return getParameters("CUSTOMER_FIAT_REMITTANCE_PAGE"); }

	//Ops Pages
	public static String getOPSLoginPage() throws Exception{		return getParameters("OPS_LOGIN_PAGE"); }
	public static String getOperationsDashboardPage() throws Exception{		return getParameters("OPS_DASHBOARD_PAGE"); }
	public static String getOpsErrorPage() throws Exception{		return getParameters("OPS_ERROR_PAGE"); }
	public static String getOpsPendingMerchantPage() throws Exception{		return getParameters("OPS_PENDING_MERCHANT_PAGE"); }
	public static String getOpsEditSpecificMerchanPage() throws Exception{		return getParameters("OPS_EDIT_SPECIFIC_MERCHANT_PAGE"); }
	public static String getOpsMerchantRiskProfilePage() throws Exception{		return getParameters("OPS_MERCHANT_RISK_PROFILE_PAGE"); }
	public static String getOpsMerchanMCCPage() throws Exception{		return getParameters("OPS_MERCHANT_MCC_GROUP_PAGE"); }
	public static String getOpsAllMerchantPage() throws Exception{		return getParameters("OPS_ALL_MERCHANT_PAGE"); }
	public static String getOpsPendingCustomerPage() throws Exception{		return getParameters("OPS_CUSTOMER_PENDING_PAGE"); }
	public static String getOpsEditSpecificCustomerPage() throws Exception{		return getParameters("OPS_CUSTOMER_EDIT_CUSTOMER_PAGE"); }
	public static String getOpsAllCustomerDetailsPage() throws Exception{		return getParameters("OPS_CUSTOMER_ALL_CUSTOMER_PAGE"); }
	public static String getOpsSetPricingPage() throws Exception{		return getParameters("OPS_SETPRICING_PAGE"); }
	public static String getOpsTransactionLimitsPage() throws Exception{		return getParameters("OPS_TRANSACTION_LIMITS_PAGE"); }
	public static String getOpsViewLoyaltyPage() throws Exception{		return getParameters("OPS_VIEW_LOYALTY_PAGE"); }
	public static String getOpsViewCustomerLoyaltyPage() throws Exception{		return getParameters("OPS_VIEW_CUSTOMER_LOYALTY_PAGE"); }
	public static String getOpsViewMerchantPlanPage() throws Exception{		return getParameters("OPS_VIEW_MERCHANT_PLAN_PAGE"); }
	public static String getOpsSysProfilePage() throws Exception{		return getParameters("OPS_VIEW_PROFILE_PAGE"); }
	public static String getOpsManageUsersPage() throws Exception{		return getParameters("OPS_MANAGE_USERS_PAGE"); }
	public static String getOpsCustViewWalletPage() throws Exception{		return getParameters("OPS_CUST_VIEW_WALLET_PAGE"); }
	public static String getOpsAllDisputeReasonsPage() throws Exception{		return getParameters("OPS_DISPUTE_REASON_PAGE"); }
	public static String getViewCustomerDisputePageOps() throws Exception{		return getParameters("OPS_SHOW_CUSTOMERS_DISPUTES_PAGE"); }
	public static String getOpsSpecificCustomerDisputePage() throws Exception{		return getParameters("OPS_SHOW_CUSTOMER_SPECIFIC_DISPUTES_PAGE"); }
	public static String getViewMerchantDisputePageOps() throws Exception{		return getParameters("OPS_VIEW_MERCHANT_DISPUTES_PAGE");} 
	public static String getOpsRaiseDisputePage() throws Exception{		return getParameters("OPS_RAISE_DISPUTES_PAGE"); }
	public static String getOpsViewTransactionRulePage() throws Exception{		return getParameters("OPS_VIEW_TRANSACTION_RULE_PAGE"); }
	public static String getSlabRatesForSpecificPlan() throws Exception{		return getParameters("OPS_VIEW_SLAB_RATE_FORPLAN_PAGE"); }
	
	public static String getOpsViewWalletsPage() throws Exception{		return getParameters("OPS_VIEW_WALLETS_PAGE"); }
	public static String getOpsViewSpecificCustomerWalletsPage() throws Exception{		return getParameters("OPS_VIEW_SPECIFIC_CUSTOMER_WALLETS_PAGE"); }
	public static String getOpsVieCustomerTokenizedCardsPage() throws Exception{		return getParameters("OPS_VIEW_CUSTOMER_TOKENIZED_PAGE"); }
	public static String getOpsViewSpecificCustomerTokenizedCardsPage() throws Exception{		return getParameters("OPS_VIEW_SPECIFIC_CUSTOMER_TOKENIZED_PAGE"); }
	public static String getOpsViewWalletAssetsPage() throws Exception{		return getParameters("OPS_VIEW_WALLET_ASSETS_PAGE"); }
	public static String getOpsFiatWalletTransactionsPage() throws Exception{		return getParameters("OPS_FIAT_WALLET_TRANSACTION_PAGE"); }
	public static String getOptWalsCryptoletTransactionsPage() throws Exception{		return getParameters("OPS_CRYPTO_WALLET_TRANSACTION_PAGE"); }
	public static String getOptWalsCardTransactionsPage() throws Exception{		return getParameters("OPS_CARD_WALLET_TRANSACTION_PAGE"); }
	public static String getOpsViewCardsPage() throws Exception{		return getParameters("OPS_VIEW_CARDS_PAGE"); }
	public static String getOpsViewPorteRequestPage() throws Exception{		return getParameters("OPS_VIEW_PORTE_REQUEST_PAGE"); }
	public static String getOpsGetPorteTransactions() throws Exception{		return getParameters("OPS_VIEW_PORTE_TRANSACTIONS_PAGE"); }
	public static String getOpsViewSpecificUserPortTxn() throws Exception{		return getParameters("OPS_VIEW_SPECIFIC_USER_PORTE_TXN_PAGE"); }
	public static String getOpsViewPorteWalletsPage() throws Exception{		return getParameters("OPS_VIEW_PORTE_WALLETS_PAGE"); }
	public static String getOpsViewSpecificUserPortWal() throws Exception{		return getParameters("OPS_VIEW_SPECIFIC_USER_PORTE_WAL_PAGE"); }
		
	public static String getOpsBlockCodePage() throws Exception{		return getParameters("OPS_BLOCK_CODE_PAGE"); }
	public static String getFundRegisteredAccountsPage() throws Exception{		return getParameters("OPS_FUND_REGISTERED_ACCOUNTS_PAGE"); }
	public static String getCreateOffersPage() throws Exception{		return getParameters("OPS_CREATE_OFFERS_PAGE"); }
	public static String getViewadEditOffersPage() throws Exception{		return getParameters("OPS_VIEW_EDIT_PAGE"); }
	public static String getFundedAccountsAccountsPage() throws Exception{		return getParameters("OPS_ALL_FUNDED_ACCOUNTS"); }
	public static String getOpsBlockWalletsPage() throws Exception{		return getParameters("OPS_BLOCK_WALLETS_PAGE"); }
	
	public static String getOpsBusinessLedgerPage() throws Exception{		return getParameters("OPS_BUSINESS_LEDGER_PAGE"); }
	public static String getOpsSystemAuditTrailPage() throws Exception{		return getParameters("OPS_SYSTEM_AUDIT_TRAIL_PAGE"); }
	public static String getOpsDistributionPage() throws Exception{		return getParameters("OPS_VIEW_DISTRIBUTION_PAGE"); }
	public static String getOpsLiquidityAccountPage() throws Exception{		return getParameters("OPS_VIEW_LIQUIDITY_ACCOUNT_PAGE"); }
	public static String getOpsViewBlockchainTransactionsPage() throws Exception{		return getParameters("OPS_VIEW_BLOCKCHAIN_TRANSACTIONS_PAGE"); }
	public static String getOpsAuthorizedWalletTransactionsPage() throws Exception{		return getParameters("OPS_VIEW_AUTHORIZED_WALLET_TRANSACTION_PAGE"); }

	public static String getOpsViewManageFunctionsPage() throws Exception{		return getParameters("OPS_VIEW_MANAGE_FUNCTIONS_PAGE");	}
	public static String getOpsManagePricingPlansPage() throws Exception{		return getParameters("OPS_MANAGE_PRICING_PLAN_PAGE");		}
	public static String getOpsAllocateRulestoPlansPage() throws Exception{		return getParameters("OPS_RULES_TO_PLAN_ALLOCATE_PAGE");	}
	public static String getOpsSetPasswordPage() throws Exception{		return getParameters("OPS_SET_PASSWORD_PAGE");	}
	//Remittance Module ops
	public static String getOpsViewPartnersPage() throws Exception{		return getParameters("OPS_VIEW_PARTNERS_PAGE"); }
	
	public static String getOpsPricingPlansAllocatePage() throws Exception{		return getParameters("OPS_PRICING_PLAN_ALLOCATE_PAGE");	}
	public static String getOpsCreateClaimableBalancePage() throws Exception{		return getParameters("OPS_CREATE_CLAIMABLE_BALANCE_PAGE");	}
	public static String getRemittanceTransactionPage() throws Exception{		return getParameters("OPS_REMITTANCE_TRANSACTION_PAGE");	}
	
	//iteration five
	public static String getOpsWalletAssetAccountsPage() throws Exception{		return getParameters("OPS_WALLET_ASSET_ACCOUNTS_PAGE");	}
	
	//Partners module
	public static String getPartnersDashboardPage() throws Exception{		return getParameters("PART_DASHBOARD_PAGE"); }
	public static String getPartnersProfilePage() throws Exception{		return getParameters("PART_PARTNERS_PROFILE_PAGE"); }
	public static String getPartnersPendingTransactionPage() throws Exception{		return getParameters("PART_PARTNERS_PENDING_TRANSACTION_PAGE"); }
	public static String getPartnersCompleteTransactionPage() throws Exception{		return getParameters("PART_PARTNERS_COMPLETE_TRANSACTION_PAGE"); }
	public static String getPartnersViewIssuesPage() throws Exception{		return getParameters("PART_PARTNERS_VIEW_ISSUERS_PAGE"); }
	public static String getPartnerSetPasswordPage() throws Exception{		return getParameters("PART_PARTNERS_SET_PASSWORD_PAGE"); }
	
	// TDA Module
	public static String getTDADashboardPage() throws Exception { return getParameters("TDA_DASHBOARD_PAGE");}
	public static String getTDAAccountConfigurationPage() throws Exception { return getParameters("TDA_ACCOUNT_CONFIGURATION_PAGE");}
	public static String getTDAFiatToBTCxPricingPage() throws Exception { return getParameters("TDA_FIAT_TO_BTCx_PRICING_PAGE");}
	public static String getTDAPorteAssetToBTCxPricingPage() throws Exception { return getParameters("TDA_PORTE_ASSETS_TO_BTCX_PRICING_PAGE");}
	public static String getTDARequestFromFiatPage() throws Exception { return getParameters("TDA_REQUEST_FROM_FIAT_PAGE");}
	public static String getTDARequestFromPorteAssetsPage() throws Exception { return getParameters("TDA_REQUEST_FROM_PORTE_ASSETS_PAGE");}
	public static String getTDAPurchaseBtcxPage() throws Exception { return getParameters("TDA_PURCHASE_BTCX_PAGE");}
	public static String getTDADisplayStellarTransactionsPage() throws Exception { return getParameters("TDA_DISPLAY_STELLAR_TRANSACTION_PAGE");}
	public static String getTDABTCAccountInformationPage() throws Exception { return getParameters("TDA_BTC_ACCOUNT_INFORMATION_PAGE");}
	public static String getTDAFiatToBTCPricingPage() throws Exception { return getParameters("TDA_FIAT_TO_BTC_PRICING_PAGE");}
	public static String getTDABitcoinToStellarBitcoinPricingPage() throws Exception { return getParameters("TDA_BITCOIN_TO_STELLAR_BITCOIN_PRICING_PAGE");}
	public static String getTDAFiatToBTCRequestPage() throws Exception { return getParameters("TDA_FIAT_TO_BTC_REQUEST_PAGE");}
	public static String getTDABitcoinToStellarBitcoinRequestPage() throws Exception { return getParameters("TDA_BITCOIN_TO_STELLAR_BITCOIN_REQUEST_PAGE");}
	public static String getTDADisplayBitcoinTransactionPage() throws Exception { return getParameters("TDA_DISPLAY_BTC_TRANSACTIONS_PAGE");}
	public static String getTDADisplayFiatRemittanceTxnPage() throws Exception { return getParameters("TDA_DISPLAY_FIAT_REMITTANCE_TRANSACTIONS_PAGE");}
	
	// Merchant Pages
	public static String getMerchantDashboardPage() throws Exception{				return getParameters("MERCHANT_DASHBOARD_PAGE"); }
	public static String getMerchantProfilePage() throws Exception{				return getParameters("MERCHANT_PROFILE_PAGE"); }
	public static String getMerchantManageBranchesPage() throws Exception{				return getParameters("MERCHANT_MANAGE_BRANCHES_PAGE"); }
	public static String getMerchantManageUsersPage() throws Exception{				return getParameters("MERCHANT_MANAGE_USERS_PAGE"); }
	public static String getMerchantAddBranchPage() throws Exception{				return getParameters("MERCHANT_ADD_BRANCH_PAGE"); }
	public static String getMerchantStorePaymentPage() throws Exception{				return getParameters("MERCHANT_STORE_PAYMENTS_PAGE"); }
	public static String getMerchantCashOutPage() throws Exception{				return getParameters("MERCHANT_CASH_OUT_PAGE"); }
	public static String getMerchantTopUpPage() throws Exception{				return getParameters("MERCHANT_TOP_UP_PAGE"); }
	public static String getMerchantRaiseDisputePage() throws Exception{				return getParameters("MERCHANT_RAISE_DISPUTE_PAGE"); }
	public static String getMerchantViewDisputePage() throws Exception{				return getParameters("MERCHANT_VIEW_DISPUTE_PAGE"); }
	public static String getMerchantViewSpecificDisputePage() throws Exception{				return getParameters("MERCHANT_VIEW_SPECIFIC_DISPUTE_PAGE"); }
	public static String getMerchantTransctionHistoryPagePage() throws Exception{				return getParameters("MERCHANT_TRANSCTION_HISTORY_PAGE"); }
	
	//TRANSACTION RULES-- 
	public static String getTokenRegistrationCodeForCustomer() throws Exception{				return getParameters("CODE_TOKEN_REGISTRATION_CARD_CUSTOMER"); }
	public static String getCodeTokenWalletTopup() throws Exception{				return getParameters("CODE_TOKEN_WALLET_TOPUP"); }
	public static String getAmountForTokenRegistration() throws Exception{				return TOKEN_REGISTRATION_AMOUNT ;}
	public static String getCodeBitCoinP2P() throws Exception{				return getParameters("CODE_BITCOIN_WALLET_P2P"); }
	public static String getCodeEthereumCoinP2P() throws Exception{				return getParameters("CODE_ETHEREUM_WALLET_P2P"); }
	public static String getCodeLitecoinCoinP2P() throws Exception{				return getParameters("CODE_LITECOIN_WALLET_P2P"); }
	public static String getCodeBuyBitCoin() throws Exception{				return getParameters("CODE_BUY_BITCOIN"); }
	public static String getCodeBuyEthereumCoin() throws Exception{				return getParameters("CODE_BUY_ETHEREUM"); }
	public static String getCodeBuyLitecoinCoin() throws Exception{				return getParameters("CODE_BUY_LITECOIN"); }
	public static String getCodeSellBitCoin() throws Exception{				return getParameters("CODE_SELL_BITCOIN"); }
	public static String getCodeSellEthereumCoin() throws Exception{				return getParameters("CODE_SELL_ETHEREUM"); }
	public static String getCodeSellLitecoinCoin() throws Exception{				return getParameters("CODE_SELL_LITECOIN"); }
	public static String getCodeExchangePorteAssetForBTCx() throws Exception{				return getParameters("CODE_EXCHANGE_PORTE_ASSETS_FOR_BTCX"); }
	//
	public static String getCodeFiatWalletP2P() throws Exception{				return getParameters("CODE_FIAT_WALLET_P2P"); }
	public static String getCodePorteUtilityCoinP2P() throws Exception{				return getParameters("CODE_PORTE_UTILITY_COIN_P2P"); }
	public static String getCodeVesselCoinP2P() throws Exception{				return getParameters("CODE_VESSEL_COIN_P2P"); }
	//Buy Assets Via Token
	public static String getCodeBuyPorteCoinViaToken() throws Exception{				return getParameters("CODE_BUY_PORTE_COIN_VIA_TOKEN"); }
	public static String getCodeBuyVesselCoinViaToken() throws Exception{				return getParameters("CODE_BUY_VESSEL_COIN_VIA_TOKEN"); }
	public static String getCodeBuyXLMViaToken() throws Exception{				return getParameters("CODE_BUY_XLM_VIA_TOKEN"); }
	public static String getCodeBuyUSDCViaToken() throws Exception{				return getParameters("CODE_BUY_USDC_VIA_TOKEN"); }
	public static String getCodeBuyBTCxViaToken() throws Exception{				return getParameters("CODE_BUY_BTCX_VIA_TOKEN"); }
	public static String getCodeBuyBTCxViaFiat() throws Exception{				return getParameters("CODE_BUY_BTCX_VIA_FIAT"); }
	
	public static String getCodeBuyBTCViaToken() throws Exception{				return getParameters("CODE_BUY_BTC_VIA_TOKEN"); }
	public static String getCodeBuyBTCViaFiatWallet() throws Exception{				return getParameters("CODE_BUY_BTC_VIA_FIAT_WALLET"); }
	//Buy Assets Via Wallet
	public static String getCodeBuyPorteCoinViaFiatWallet() throws Exception{				return getParameters("CODE_BUY_PORTE_COIN_VIA_FIAT_WALLET"); }
	public static String getCodeBuyVesselCoinViaFiatWallet() throws Exception{				return getParameters("CODE_BUY_VESSEL_COIN_VIA_FIAT_WALLET"); }
	public static String getCodeBuyXLMViaFiatWallet() throws Exception{				return getParameters("CODE_BUY_XLM_VIA_FIAT_WALLET"); }
	public static String getCodeBuyUSDCViaFiatWallet() throws Exception{				return getParameters("CODE_BUY_USDC_VIA_FIAT_WALLET"); }
	
	public static String getCodeOfframpPorteCoinToFiatWallet() throws Exception{				return getParameters("CODE_OFFRAMP_PORTE_COIN_TO_FIAT_WALLET"); }
	public static String getCodeOfframpVesselCoinToFiatWallet() throws Exception{				return getParameters("CODE_OFFRAMP_VESSEL_COIN_TO_FIAT_WALLET"); }
	public static String getCodeOfframpUSDCToFiatWallet() throws Exception{				return getParameters("CODE_OFFRAMP_USDC_TO_FIAT_WALLET"); }

	
	public static String getCodeSellVesselCoin() throws Exception{				return getParameters("CODE_SELL_VESSEL_COIN"); }
	public static String getCodeSellPorteCoin() throws Exception{				return getParameters("CODE_SELL_PORTE_COIN"); }
	public static String getCodeUSDCCoinP2P() throws Exception{				return getParameters("CODE_USDC_COIN_P2P"); }
	public static String getCodeXLMCoinP2P() throws Exception{				return getParameters("CODE_XLM_COIN_P2P"); }
	// New- TODO- Make sure this new Transaction rule is updated in the database
	public static String getCodeCustomerCurrencyRemittance() throws Exception{				return getParameters("CODE_CUSTOMER_CURRENCY_REMITTANCE"); }
	// New- TODO- Make sure this new Transaction rule is updated in the database
	public static String getCodeTDASwapPorteAssetsForBTCx() throws Exception{				return getParameters("CODE_TDA_SWAP_PORTE_ASSETS_FOR_BTCX"); }
	public static String getCodeCustomerBuyPlanViaWallet() throws Exception{				return getParameters("CODE_CUSTOMER_BUY_PLAN_VIA_WALLET"); }
	public static String getCodeCustomerBuyPlanViaToken() throws Exception{				return getParameters("CODE_CUSTOMER_BUY_PLAN_VIA_TOKEN"); }
	public static String getFiatRemittanceTxnCode() throws Exception{				return getParameters("CODE_FIAT_REMMITTACE"); }
	
	
	//Stellar
//	public static String getPorteIssuerAccountId() throws Exception{				return getParameters("PORTE_ISSUER_ACCOUNT_ID"); }
//	public static String getVesselIssuerAccountId() throws Exception{				return getParameters("VESL_ISSUER_ACCOUNT_ID"); }
//	public static String getBTCIssuerAccountId() throws Exception{				return getParameters("BTC_ISSUER_ACCOUNT_ID"); }
//	public static String getUSDCIssuerAccountId() throws Exception{				return getParameters("USDC_ISSUER_ACCOUNT_ID"); }
//	public static String getPorteDistributorAccountId() throws Exception{				return getParameters("PORTE_DISTRIBUTION_ACCOUNT_ID"); }
//	public static String getVesselDistributorAccountId() throws Exception{				return getParameters("VESL_DISTRIBUTION_ACCOUNT_ID"); }
//	public static String getUSDCDistributorAccountId() throws Exception{				return getParameters("USDC_DISTRIBUTION_ACCOUNT_ID"); }
//	public static String getXLMDistributorAccountId() throws Exception{				return getParameters("XLM_DISTRIBUTION_ACCOUNT_ID"); }
//	public static String getPorteLiquidityAccountId() throws Exception{				return getParameters("PORTE_LIQUIDITY_ACCOUNT_ID"); }
//	public static String getVesselLiquidityAccountId() throws Exception{				return getParameters("VESSEL_LIQUIDITY_ACCOUNT_ID"); }
	public static String getMaxStellarAssetWalletLimit() throws Exception{				return getParameters("STELLAR_ASSET_ACCOUNT_LIMIT"); }
	public static String getFriendBootStellarSDKUrl() throws Exception{				return getParameters("FRIEND_BOOT_STELLAR_URL"); }
	public static String getStellarTestEviromentUrl() throws Exception{				return getParameters("STELLAR_TEST_URL"); }
	public static String getPorteAccountLimit() throws Exception{				return getParameters("PORTE_ACCOUNT_LIMIT"); }
	public static String getFundPorteAccountAmount() throws Exception{				return getParameters("FUND_PORTE_ACCOUNT_AMOUNT"); }
	public static String getVesselAccountId() throws Exception{				return getParameters("VESSEL_ISSUER_ACCOUNT_ID"); }
	public static String getDigitalCurrencyIssuerAccountId() throws Exception{				return getParameters("DIGITAL_CURRENCY_ISSUER_ACCOUNT_ID"); }
	public static String getStellarCustomerTransactionLimit () throws Exception{				return STELLAR_CUSTOMER_TRANSACTION_LIMIT; }
	public static String getStellarTdaTransactionLimit () throws Exception{				return STELLAR_TDA_TRANSACTION_LIMIT; }

	public static String getOpsViewAssetPricingPage() throws Exception{		return getParameters("OPS_VIEW_ASSET_PRICING_PAGE"); }
	public static String getOpsViewLoyaltyRatesPage() throws Exception{		return getParameters("OPS_VIEW_LOYALTY_RATE_PAGE"); }
	public static String getCodeLoyaltyRedemption() throws Exception{				return getParameters("CODE_LOYALTY_REDEMPTION"); }
	
	public static String getInvoiceFilePath() throws Exception { 				return getParameters("PARTNER_INVOICE_FILE_PATH");}
	public static String getPorteLogoPath() throws Exception { 				return getParameters("PORTE_LOGO_PATH");}
	
	 // BlockCypher APIs
	public static String getBlockCypherBitcoinMainNetURL() throws Exception { return getParameters("BLOCKCYPHER_BITCOIN_MAINNET_URL");}
	public static String getBlockCypherBitcoinTest3NetURL() throws Exception { return getParameters("BLOCKCYPHER_BITCOIN_TEST3NET_URL");}
	public static String getBlockCypherAccessToken() throws Exception { return getParameters("BLOCKCYPHER_ACCESS_TOKEN");}

	//Bitcoin module
	public static String getBitCoinEnviroment() throws Exception { 				return getParameters("GET_BITCOIN_ENVIROMENT");}
	public static String getBitCoinCreateTransactionBlockCypherUrl() throws Exception { 				return getParameters("CREATE_TRANSACTION_BLOCKCYPHER_URL");}
	public static String getBitCoinSignTransactionBlockCypherUrl() throws Exception { 				return getParameters("SIGN_TRANSACTION_BLOCKCYPHER_URL");}
	public static String getBitCoinBlockCypherAPIToken() throws Exception { 				return getParameters("BLOCKCYPHER_API_TOKEN");}
	public static String getBTCP2PTxnCode() throws Exception { 				return getParameters("CODE_CUSTOMER_BTC_P2P");}
	public static String getBTCToBTCxSwapCode() throws Exception { 				return getParameters("CODE_CUSTOMER_BTC_TO_BTCX_SWAP");}
	public static String getBlockCypherBaseUrl() throws Exception { 				return getParameters("BLOCK_CYPHER_BASE_URL");}
	
    public static synchronized JSONObject getInstance(){ 
    	return JSON_LOCALE; 
    	}
    public static synchronized void setInstance(JSONObject JLocale){ 
    	JSON_LOCALE = JLocale;
    	}
    public static synchronized void init() throws Exception {
    	JSONParser parser = null;
    	try {
    		if(JSON_LOCALE==null) {
       		 	parser = new JSONParser();
    		       //Properties jvm = System.getProperties();
    		       // jvm.list(System.out);
	 					System.out.print(className+ " *** catalina.home is**** "+System.getProperty("catalina.home")+"\n");
	 					try {
	 			         System.out.println(className+ " *** Now Reading Properties file**** ");	 			         	 			         
	 			         Object objRead = parser.parse(new FileReader
	 			        		 (StringUtils.replace(System.getProperty("catalina.home"), "\\", "/")+"/PPApplicationParameters.json"));
	 			        //Object objRead = parser.parse(new FileReader("D:/apache-tomcat-9.0.7-2/CPBooksApplicationParameters.json"));
	 			         JSON_LOCALE = (JSONObject) objRead;
	 			        //System.out.println("\n "+JSON_LOCALE.toString());
		 				} catch (Exception e) {
		 					System.out.println(className+ "  Exception in reading Resourcebundle "+e.getMessage());
		 				}
			     			debugOn = Boolean.parseBoolean(StringUtils.trim(EncryptionHandler.decryptJson(JSON_LOCALE.get("debugOn").toString()))); 
		     			if(isWindows()){
		     				FILE_UPLOAD_PATH = StringUtils.trim(EncryptionHandler.decryptJson(JSON_LOCALE.get("UPLOAD_PATH_WIN").toString()));
		     				FILE_DOWNLOAD_PATH = StringUtils.trim(EncryptionHandler.decryptJson(JSON_LOCALE.get("DOWNLOAD_PATH_WIN").toString()));
		     				LOGBACK_CONFIG_FILE_PATH = StringUtils.replace(System.getProperty("catalina.home"), "\\", "/") +	StringUtils.trim(EncryptionHandler.decryptJson(JSON_LOCALE.get("ERROR_LOG_WIN").toString()));
		     			}else if(isUnix()){
		     				FILE_UPLOAD_PATH = StringUtils.trim(EncryptionHandler.decryptJson(JSON_LOCALE.get("UPLOAD_PATH_LIN").toString()));
		     				FILE_DOWNLOAD_PATH = StringUtils.trim(EncryptionHandler.decryptJson(JSON_LOCALE.get("DOWNLOAD_PATH_LIN").toString()));
		     	 			LOGBACK_CONFIG_FILE_PATH = StringUtils.replace(System.getProperty("catalina.home"), "\\", "/") + StringUtils.trim(EncryptionHandler.decryptJson(JSON_LOCALE.get("ERROR_LOG_LIN").toString()));
		     			}else if(isSolaris()){
		     				FILE_UPLOAD_PATH = StringUtils.trim(EncryptionHandler.decryptJson(JSON_LOCALE.get("UPLOAD_PATH_LIN").toString()));
		     				FILE_DOWNLOAD_PATH = StringUtils.trim(EncryptionHandler.decryptJson(JSON_LOCALE.get("DOWNLOAD_PATH_LIN").toString()));
		     	 			LOGBACK_CONFIG_FILE_PATH = StringUtils.replace(System.getProperty("catalina.home"), "\\", "/") + StringUtils.trim(EncryptionHandler.decryptJson(JSON_LOCALE.get("ERROR_LOG_LIN").toString()));
		     			}
		     				//**** Now forming the logger file
		     			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
	     	 			PatternLayoutEncoder ple = new PatternLayoutEncoder();
	     	 			ple.setPattern("%-12date{dd-MM-YYYY HH:mm:ss.SSS} - %msg%n");
	     	 			ple.setContext(lc);
	     	 			ple.start();
	     	 			FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
	     	 			fileAppender.setFile(LOGBACK_CONFIG_FILE_PATH);
	     	 			fileAppender.setEncoder(ple);
	     	 			fileAppender.setContext(lc);
	     	 			fileAppender.start();
	     	 			logger = (Logger) LoggerFactory.getLogger("");
	     	 			logger.addAppender(fileAppender);
	     	 			logger.setLevel(Level.WARN);
	     	 			logger.setAdditive(false);
					
		      			
		 			
    		}else {
    			setComment(3, className, "Environment already formed...");
    		}
    		
    	}catch(Exception e) {
    		System.out.println(className+"  ==> Exception in the init() method -- > "+e.getMessage());
    	}finally {
    		if(parser!=null) parser = null;
    	}
    	
    }

    public static void setComment(int level,String className, String msg) { 
		  try{
			  if(debugOn) {
				  switch(level) {
				  case 1:   logger.warn("WARN:  ["+className+"]: "+msg);				  break;
				  case 2:   logger.warn("DEBUG: ["+className+"]: "+msg);				  break;
				  case 3:   logger.warn("INFO:  ["+className+"]: "+msg);				  break;
				  }
				  }else {
					  if(level==1)
						    logger.warn("WARN:  ["+className+"] --- "+msg);	
				  }
				  
	      	} catch (Exception e){
	      		System.out.println(className+"  ==> Exception in the setComment() method -- > "+e.getMessage());
	      	}
	}
   
	//private static String getDBpass1()  throws Exception{return Utilities.getPass(DBPASS1.trim());	}
 	public static boolean isWindows() { return (OS.indexOf("win") >= 0); }
 	public static boolean isMac() { return (OS.indexOf("mac") >= 0); 	}
 	public static boolean isUnix() { return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ); 	}
 	public static boolean isSolaris() { return (OS.indexOf("sunos") >= 0); 	}

	private static synchronized String getParameters(String paramName) throws Exception{		return StringUtils.trim(EncryptionHandler.decryptJson(JSON_LOCALE.get(paramName).toString()));	}

}
