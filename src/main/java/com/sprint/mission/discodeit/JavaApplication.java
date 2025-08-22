package com.sprint.mission.discodeit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class JavaApplication {
    public static void main(String[] args) {


        //[ ] 등록
        //[ ] 조회(단건, 다건)
        //[ ] 수정
        //[ ] 수정된 데이터 조회
        //[ ] 삭제
        //[ ] 조회를 통해 삭제되었는지 확인
        Scanner scanner = new Scanner(System.in);
        HashMap<String, String> userMap = new HashMap<>(); // ID와 이름 저장 HashMap
        HashMap<String, String> userMap1 = new HashMap<>(); // ID와 메세지 저장할 HashMap
        ArrayList<String> idList = new ArrayList<>(); // ID만 저장할 ArrayList

        while (true) {
            System.out.println("== 메뉴 선택 ==");
            System.out.println("1. 등록");
            System.out.println("2. 조회");
            System.out.println("3. 수정");
            System.out.println("4. 삭제");
            System.out.println("5. 종료");
            System.out.print("번호 선택: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); //nextInt() 후 남은 개행 문자 처리

            switch (choice) {
                case 1:
                    // 등록
                    System.out.print("등록할 ID를 입력하세요: ");
                    String id = scanner.nextLine();
                    System.out.print("이름을 입력하세요: ");
                    String name = scanner.nextLine();
                    System.out.print("[channel 1] 메시지를 입력하세요: ");
                    String message = scanner.nextLine();

                    // ID 중복 확인
                    if (userMap.containsKey(id)) {
                        System.out.println("이미 등록된 ID입니다.");
                    } else {
                        userMap.put(id, name);
                        userMap1.put(id, message); // message추가
                        idList.add(id);
                        System.out.println("등록 성공!!!!!");
                    }
                    break;

                case 2:
                    // 조회
                    System.out.print("조회할 ID를 입력하세요: ");
                    String searchId = scanner.nextLine();

                    if (userMap.containsKey(searchId)) {
                        String searchName = userMap.get(searchId);
                        String searchMessage = userMap1.get(searchId);
                        System.out.println("ID: " + searchId + ", 이름: " + searchName
                                + ", 남긴 메세지: " + searchMessage);
                    } else {
                        System.out.println("해당 ID의 회원이 없습니다.");
                    }
                    break;

                case 3:
                    // 수정
                    System.out.print("메세지 수정을 위해, ID 를  입력하세요: ");
                    String searchId1 = scanner.nextLine();


                    if (userMap.containsKey(searchId1)) {
                        String searchMessage = userMap1.get(searchId1);
                        System.out.println("ID: " + searchId1 + ", 현재 메세지: "
                                + searchMessage);
                    } else {
                        System.out.println("해당 name 은 존재하지 않습니다.");
                    }
                    System.out.print("수정할 메세지 내용을 입력하세요.: ");
                    String updateMessage = scanner.nextLine();
                    userMap1.put(searchId1, updateMessage);
                    break;

                case 4:
                    // 삭제 (사용자,메세지 삭제)
                    System.out.print("삭제할 ID를 입력하세요: ");
                    String removeId = scanner.nextLine();



                    //
                    if (userMap.containsKey(removeId)) {

                        userMap.remove(removeId); // 아이디 삭제
                        userMap1.remove(removeId);

                        System.out.println("삭제 성공!!!!!");
                    }
                    break;

                case 5:
                    // 종료
                    System.out.println("프로그램을 종료합니다.");
                    scanner.close();
                    return;

                default:
                    System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
            }
        }

    }
}
