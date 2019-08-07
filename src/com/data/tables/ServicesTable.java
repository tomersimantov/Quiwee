package com.data.tables;
import java.util.List;
import com.data.Pullable;
import com.util.data.DataType;
import com.util.data.MysqlColumn;

public enum ServicesTable implements Pullable
{
	USER_ID(new MysqlColumn("user_id", DataType.VARCHAR)),
	NAME(new MysqlColumn("name", DataType.VARCHAR)),
	PRICE(new MysqlColumn("price", DataType.DECIMAL)),
	HOURLY_PRICE(new MysqlColumn("hourly", DataType.BOOLEAN));
	
	private MysqlColumn column;
	private List<Object> list;
	
	private ServicesTable(MysqlColumn column) {
		this.column = column;
	}
	
	@Override
	public List<Object> getList() { return list; }
	
	@Override
	public void setList(List<Object> l) { list = l; }

	@Override
	public String getTableName() { return "services"; }

	@Override
	public MysqlColumn getColumn() { return column; }
	
	public static Pullable[] getValues() { return values(); }
}