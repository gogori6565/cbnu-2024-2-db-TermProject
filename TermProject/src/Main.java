import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String args[]) {
        try {
            // Mysql JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Mysql 데이터베이스 연결
            Connection con = DriverManager.getConnection(
                    "[url]",
                    "[username]", "[password]");
            Statement stmt = con.createStatement();
            Scanner scanner = new Scanner(System.in);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}