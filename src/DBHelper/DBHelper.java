package DBHelper;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.ResultSupport;

public class DBHelper {
	private String sql;

	public void setSql(String sql){
		this.sql=sql;
	}
	private List sqlValues;//sql语句的参数
	public void setSqlValues(List sqlValues){
		this.sqlValues=sqlValues;
	}
	private Connection con;//链接对象
	public void setConnection(Connection con){
		this.con=con;
	}
	public DBHelper(){
		this.con=getConnection();//给Connection的对象赋初值  
	}
	/*
	 * 获取数据库连接
	 */
	private Connection getConnection(){
		String driver_class=null;
		String drvier_url=null;
		String database_user=null;
		String database_password=null;
		try{
			InputStream fis=this.getClass().getResourceAsStream("/sqlserver.properties");//加载数据库配置文件到内存中
			//InputStream fis=this.getClass().getResourceAsStream("/mysql.properties");
			Properties p=new Properties();
			p.load(fis);
			
			driver_class=p.getProperty("driver_class");
			drvier_url=p.getProperty("driver_url");			
			database_user=p.getProperty("database_user");
			database_password=p.getProperty("database_password");
			
			Class.forName(driver_class);
			con=DriverManager.getConnection(drvier_url,database_user,database_password);
			
	   }
	   catch (ClassNotFoundException e) {  
        // TODO Auto-generated catch block  
          e.printStackTrace();  
       } catch (SQLException e) {  
        // TODO Auto-generated catch block  
          e.printStackTrace();  
       } catch (FileNotFoundException e) {  
        // TODO Auto-generated catch block  
        e.printStackTrace();  
       } catch (IOException e) {  
        // TODO Auto-generated catch block  
        e.printStackTrace();  
       }
		return con;
	}
	/*
	 * 关闭数据库连接
	 */
	private void closeAll(Connection con,PreparedStatement pst,ResultSet rst){
		if(rst!=null){
			try{
				rst.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			
		}
		if(pst!=null){
			try{
				pst.close();
			}
			catch(SQLException e){
				e.printStackTrace();
			}
			
		}
		if(con!=null){
			try{
				con.close();
			}
			catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	/*
	 * 查找
	 */
	public Result executeQuery(){
		Result result=null;
		ResultSet rst=null;
		PreparedStatement pst=null;
		try{
			pst=con.prepareStatement(sql);
			if(sqlValues!=null&&sqlValues.size()>0){ //当sql语句中存在占位符时  
				setSqlValues(pst,sqlValues);
			}
			rst=pst.executeQuery();
			result=ResultSupport.toResult(rst); //一定要在关闭数据库之前完成转换  
		}
		catch(SQLException e){
			e.printStackTrace();
			
		}
		finally{
			this.closeAll(con,pst,rst);
		}
		return result;
	}
	/*
	 * 增删改
	 */
	public int executeUpdate(){
		int result=-1;
		PreparedStatement pst=null;
		try{
			pst=con.prepareStatement(sql);
			 if(sqlValues!=null&&sqlValues.size()>0){  //当sql语句中存在占位符时  
	                setSqlValues(pst,sqlValues);  
	            }  
	        result=pst.executeUpdate();  
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally{
			this.closeAll(con, pst, null);
		}
		return result;
	}
	/*
	 * 给sql语句中的占位符赋值
	 */
	private void setSqlValues(PreparedStatement pst,List sqlValues){
		for(int i=0;i<sqlValues.size();i++){  
            try {  
                pst.setObject(i+1,sqlValues.get(i));  
            } catch (SQLException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
	}
	
}
