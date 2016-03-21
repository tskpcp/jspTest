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
	private List sqlValues;//sql���Ĳ���
	public void setSqlValues(List sqlValues){
		this.sqlValues=sqlValues;
	}
	private Connection con;//���Ӷ���
	public void setConnection(Connection con){
		this.con=con;
	}
	public DBHelper(){
		this.con=getConnection();//��Connection�Ķ��󸳳�ֵ  
	}
	/*
	 * ��ȡ���ݿ�����
	 */
	private Connection getConnection(){
		String driver_class=null;
		String drvier_url=null;
		String database_user=null;
		String database_password=null;
		try{
			InputStream fis=this.getClass().getResourceAsStream("/sqlserver.properties");//�������ݿ������ļ����ڴ���
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
	 * �ر����ݿ�����
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
	 * ����
	 */
	public Result executeQuery(){
		Result result=null;
		ResultSet rst=null;
		PreparedStatement pst=null;
		try{
			pst=con.prepareStatement(sql);
			if(sqlValues!=null&&sqlValues.size()>0){ //��sql����д���ռλ��ʱ  
				setSqlValues(pst,sqlValues);
			}
			rst=pst.executeQuery();
			result=ResultSupport.toResult(rst); //һ��Ҫ�ڹر����ݿ�֮ǰ���ת��  
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
	 * ��ɾ��
	 */
	public int executeUpdate(){
		int result=-1;
		PreparedStatement pst=null;
		try{
			pst=con.prepareStatement(sql);
			 if(sqlValues!=null&&sqlValues.size()>0){  //��sql����д���ռλ��ʱ  
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
	 * ��sql����е�ռλ����ֵ
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
