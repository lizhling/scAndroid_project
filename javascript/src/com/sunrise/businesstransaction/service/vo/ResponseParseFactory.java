package com.sunrise.businesstransaction.service.vo;

public class ResponseParseFactory {
	
	public static final String SHUA_KA = "101";
	public static final String CHONG_ZHENG = "102";
	public static final String READ_SIM_CARD = "103";
	public static final String READ_IC_CARD = "104";
	public static final String PRINT = "105";
	
	public static ResponseVO getResponseVO(String hex){
		ResponseVO vo = new ResponseVO(hex);
		if(SHUA_KA.equals(vo.getTypeFlag())){
			vo = new ShuaKaResponseVO(hex);
		}else if(CHONG_ZHENG.equals(vo.getTypeFlag())){
			vo = new ChongZhengResponseVO(hex);
		}else if(READ_SIM_CARD.equals(vo.getTypeFlag())){
			vo = new ReadSimCardResponseVO(hex);
		}else if(READ_IC_CARD.equals(vo.getTypeFlag())){
			vo = new ReadICCardResponseVO(hex);
		}else if(PRINT.equals(vo.getTypeFlag())){
			vo = new PrintResponseVO(hex);
		}
		
		return vo;
	}
	
}
