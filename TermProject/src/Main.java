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

            while (true) {
                System.out.println("본인의 역할을 선택하세요 (1: 학생, 2: 동아리 관리자, 3: 교수, 0: 종료): ");
                int role = scanner.nextInt();
                scanner.nextLine();

                if (role == 0) {
                    System.out.println("프로그램을 종료합니다.");
                    break;
                }

                switch (role) {
                    case 1: // 학생
                        handleStudent(con, scanner);
                        break;
                    case 2: // 동아리 관리자
                        handleClubManager(con, scanner);
                        break;
                    case 3: // 교수
                        handleProfessor(con, scanner);
                        break;
                    default:
                        System.out.println("잘못된 입력. 다시 선택해주세요.");
                }
            }

            con.close();
            scanner.close();
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 학생 기능
    private static void handleStudent(Connection con, Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("학생 메뉴 (1: 내 정보 입력, 2: 동아리 가입, 3: 프로젝트 참여, 4: 동아리 만들기, 0: 뒤로가기): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) break;

            switch (choice) {
                case 1: // 내 정보 입력
                    System.out.println("학번(SIN): ");
                    int sin = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("이름(Name): ");
                    String name = scanner.nextLine();
                    System.out.println("성별(Sex - M/F): ");
                    String sex = scanner.nextLine();
                    System.out.println("전화번호(Phone): ");
                    String phone = scanner.nextLine();

                    // 단, 역할의 경우 본인이 직접 선택 X -> 자동으로 member(부원)로 설정됨
                    PreparedStatement ps1 = con.prepareStatement(
                            "INSERT INTO Student (SIN, Name, Sex, Phone, Role) VALUES (?, ?, ?, ?, 'member')");
                    ps1.setInt(1, sin);
                    ps1.setString(2, name);
                    ps1.setString(3, sex);
                    ps1.setString(4, phone);
                    ps1.executeUpdate();
                    System.out.println("내 정보가 입력되었습니다.");
                    break;

                case 2: // 동아리 가입
                    System.out.println("학번(SIN): ");
                    sin = scanner.nextInt();
                    System.out.println("가입할 동아리 번호(Cnumber): ");
                    int cnumber = scanner.nextInt();

                    PreparedStatement ps2 = con.prepareStatement(
                            "UPDATE Student SET Cnumber = ? WHERE SIN = ?");
                    ps2.setInt(1, cnumber);
                    ps2.setInt(2, sin);
                    ps2.executeUpdate();
                    System.out.println("동아리에 가입되었습니다.");
                    break;

                case 3: // 프로젝트 참여
                    System.out.println("학번(SIN): ");
                    sin = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("참여할 프로젝트 이름(Project_name): ");
                    String pname = scanner.nextLine();

                    PreparedStatement ps3 = con.prepareStatement(
                            "INSERT INTO Participate (Ssin, Project_name) VALUES (?, ?)");
                    ps3.setInt(1, sin);
                    ps3.setString(2, pname);
                    ps3.executeUpdate();
                    System.out.println("프로젝트에 참여하였습니다.");
                    break;

                case 4: // 동아리 만들기
                    System.out.println("새 동아리 번호(Cnum): ");
                    cnumber = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("새 동아리 이름(Cname): ");
                    String cname = scanner.nextLine();
                    System.out.println("학번(SIN): ");
                    sin = scanner.nextInt();

                    PreparedStatement ps4_1 = con.prepareStatement(
                            "INSERT INTO Club (Cnum, Cname, Cnop) VALUES (?, ?, 1)");
                    ps4_1.setInt(1, cnumber);
                    ps4_1.setString(2, cname);
                    ps4_1.executeUpdate();

                    // 동아리를 만들 경우, 학생의 Role(역할)은 President(회장)으로 변경됨
                    PreparedStatement ps4_2 = con.prepareStatement(
                            "UPDATE Student SET Role = 'President', Cnumber = ? WHERE SIN = ?");
                    ps4_2.setInt(1, cnumber);
                    ps4_2.setInt(2, sin);
                    ps4_2.executeUpdate();
                    System.out.println("새 동아리가 생성되었습니다.");
                    break;

                default:
                    System.out.println("잘못된 입력. 다시 선택해주세요.");
            }
        }
    }

    // 동아리 관리자 기능
    private static void handleClubManager(Connection con, Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("동아리 관리자 메뉴 (1: 프로젝트 만들기, 2: 프로젝트 삭제하기, 0: 뒤로가기): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            if (choice == 0) break;

            switch (choice) {
                case 1: // 프로젝트 만들기
                    System.out.println("프로젝트 이름(Pname): ");
                    String pname = scanner.nextLine();
                    System.out.println("동아리 번호(Cnumber): ");
                    int cnumber = scanner.nextInt();
                    System.out.println("프로젝트 날짜(Pdate): ");
                    String pdate = scanner.next();
                    System.out.println("참여 가능 인원수(Pnop): ");
                    int pnop = scanner.nextInt();

                    PreparedStatement ps1 = con.prepareStatement(
                            "INSERT INTO Project (Pname, Cnumber, Pdate, Pnop) VALUES (?, ?, ?, ?)");
                    ps1.setString(1, pname);
                    ps1.setInt(2, cnumber);
                    ps1.setString(3, pdate);
                    ps1.setInt(4, pnop);
                    ps1.executeUpdate();
                    System.out.println("프로젝트가 생성되었습니다.");
                    break;

                case 2: // 프로젝트 삭제하기
                    System.out.println("삭제할 프로젝트 이름(Pname): ");
                    pname = scanner.nextLine();

                    PreparedStatement ps2 = con.prepareStatement(
                            "DELETE FROM Project WHERE Pname = ?");
                    ps2.setString(1, pname);
                    ps2.executeUpdate();
                    System.out.println("프로젝트가 삭제되었습니다.");
                    break;

                default:
                    System.out.println("잘못된 입력. 다시 선택해주세요.");
            }
        }
    }

    // 교수 기능
    private static void handleProfessor(Connection con, Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("교수 메뉴 (1: 내 정보 입력, 2: 동아리 지도 교수 되기, 0: 뒤로가기): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) break;

            switch (choice) {
                case 1: // 내 정보 입력
                    System.out.println("교수 번호(Anum): ");
                    int anum = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("이름(Aname): ");
                    String aname = scanner.nextLine();
                    System.out.println("이메일(Email): ");
                    String email = scanner.nextLine();

                    PreparedStatement ps1 = con.prepareStatement(
                            "INSERT INTO Advisor (Anum, Aname, Email) VALUES (?, ?, ?)");
                    ps1.setInt(1, anum);
                    ps1.setString(2, aname);
                    ps1.setString(3, email);
                    ps1.executeUpdate();
                    System.out.println("교수 정보가 입력되었습니다.");
                    break;

                case 2: // 동아리 지도 교수 되기
                    System.out.println("교수 번호(Anum): ");
                    anum = scanner.nextInt();
                    System.out.println("지도할 동아리 번호(Cnum): ");
                    int cnum = scanner.nextInt();

                    PreparedStatement ps2 = con.prepareStatement(
                            "UPDATE Club SET Anumber = ? WHERE Cnum = ?");
                    ps2.setInt(1, anum);
                    ps2.setInt(2, cnum);
                    ps2.executeUpdate();
                    System.out.println("해당 동아리의 지도 교수가 되었습니다.");
                    break;

                default:
                    System.out.println("잘못된 입력. 다시 선택해주세요.");
            }
        }
    }
}
