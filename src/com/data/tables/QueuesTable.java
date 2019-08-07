package com.data.tables;
import java.util.List;
import com.data.Pullable;
import com.util.data.DataType;
import com.util.data.MysqlColumn;

public enum QueuesTable implements Pullable
{
	USER_ID(new MysqlColumn("user_id", DataType.VARCHAR)),
	CLIENT_PHONE(new MysqlColumn("client_phone_number", DataType.VARCHAR)),
	SERVICE_NAME(new MysqlColumn("service_name", DataType.VARCHAR)),
	PRICE(new MysqlColumn("price", DataType.DECIMAL)),
	START_TIME(new MysqlColumn("start_time", DataType.DATETIME)),
	END_TIME(new MysqlColumn("end_time", DataType.DATETIME)),
	IS_CONCLUDED(new MysqlColumn("is_concluded", DataType.BOOLEAN));
	
	private MysqlColumn column;
	private List<Object> list;
	
	private QueuesTable(MysqlColumn column) {
		this.column = column;
	}
	
	@Override
	public List<Object> getList() { return list; }
	
	@Override
	public void setList(List<Object> l) { list = l; }

	@Override
	public String getTableName() { return "queues"; }

	@Override
	public MysqlColumn getColumn() { return column; }
	
	public static Pullable[] getValues() { return values(); }
}