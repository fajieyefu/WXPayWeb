package com.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.db.DBManager;

public class Service {

	public String makeOder(String card_code, String model_name,
			String model_price, String model_times, String out_trade_no) {
		String sql = "insert into model_orders(card_code,model_name,model_price,model_times,out_trade_no)values('"
				+ card_code
				+ "','"
				+ model_name
				+ "','"
				+ model_price
				+ "','"
				+ model_times + "','" + out_trade_no + "') ";
		DBManager db = DBManager.createInstance();
		db.connectDB();
		int ret = db.executeUpdate(sql);
		if (ret != 0) {
			db.closeDB();
			return "1";
		}
		db.closeDB();
		return "0";
	}

	public String changeOrder(String out_trade_no, String device_info,
			String transaction_id, String time_end) {
		String sql = "update model_orders (result,device_info,transaction_id,time_end)values(success,'"
				+ device_info
				+ "','"
				+ transaction_id
				+ "','"
				+ time_end
				+ "') where out_trade_no='" + out_trade_no + "'";
		String card_code = null;
		int model_times = 0;
		DBManager db = DBManager.createInstance();
		db.connectDB();
		int ret = db.executeUpdate(sql);
		if (ret != 0) {
			String getPayInfo = "select * from model_orders where out_trade_no='"
					+ out_trade_no + "'";
			ResultSet rs = db.executeQuery(getPayInfo);
			try {
				card_code = rs.getString("card_code");
				model_times = Integer.parseInt(rs.getString("model_times"));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String getPayTime = "select * from card_info where card_code='"
					+ card_code + "'";
			ResultSet rs1 = db.executeQuery(getPayTime);
			String push_end_time = "";
			String push_start_time = "";
			try {
				push_start_time = rs1.getString("push_start_time");
				push_end_time = rs1.getString("push_end_time");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long times = System.currentTimeMillis();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String date_string = dateFormat.format(times);
			Date date = new Date(times);
			Calendar calendar = Calendar.getInstance();
			if ((push_end_time == null)
					&& (push_end_time.compareTo(date_string) < 0)) {
				push_start_time = date_string;
				calendar.setTime(date);
				calendar.add(Calendar.MONTH, model_times);
				push_end_time = calendar.toString();
			} else {
				try {
					date = (Date) dateFormat.parse(push_end_time);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				calendar.setTime(date);
				calendar.add(Calendar.MONTH, model_times);
				push_end_time = calendar.toString();
			}
			String updateAffectTime ="update card_info set push_start_time ='"+push_start_time+"'and push_end_time='"+push_end_time+"' where card_code='"+card_code+"'";
			int ret2=db.executeUpdate(updateAffectTime);
			if(ret2!=0){
				db.closeDB();
				return "1";
			}

		}
		db.closeDB();
		return "0";
	}

	public String confrimPayResult(String card_code) {
		String sql = "select * from model_orders where card_code='" + card_code + "'";
		DBManager db = DBManager.createInstance();
		db.connectDB();
		ResultSet rs = db.executeQuery(sql);
		String result = null;
		try {
			result = rs.getString("result");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.closeDB();
		return result;
	}

}
