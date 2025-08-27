package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.entity.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;




public class FileUserService implements UserRepository, Serializable {
    private static final long serialVersionUID = 1L;

    private List<User> users = new ArrayList<>(); // 사용자 데이터를 저장할 리스트

    public FileUserService() {
        // 기본 생성자. 역직렬화 시에도 사용
    }

    @Override
    public User createUser(User user) {
        if (user.getId() == null) {
            System.out.println("UserSvc: 사용자 ID가 없습니다. 등록 실패.");
            return null; // ID가 없는 User는 등록하지 않음
        }

        // 중복 ID 방지 (선택 사항, 수동 입력 시 필수)
        if (readUser(user.getId()).isPresent()) {
            System.out.println("UserSvc: ID " + user.getId().toString().substring(0, 8) + "... 는 이미 존재하는 ID입니다. 등록 실패.");
            return null;
        }
        users.add(user);
        System.out.println("UserSvc: 새 사용자 등록 -> " + user.getName() + " (ID: " + user.getId().toString().substring(0, 8) + "...)");
        return user;
    }

   @Override
   public Optional<User> readUser(UUID Id) {
        return users.stream() // 스트림 사용,리스트에서 ID가 일치하는 사용자 찾기
                .filter(user -> user.getId().equals(Id))
                .findFirst();
    }

    @Override
    public List<User> readAllUsers() {
        return new ArrayList<>(users); // 원본 리스트의 복사본 반환 (외부에서 원본 변경 방지)
    }

    @Override
    public Optional<User> updateUser(User user) {
        Optional<User> existingUserOpt = readUser(user.getId());
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            // 전달받은 user 객체의 값으로 기존 사용자 정보 업데이트 (참조로 업데이트 됨)
            existingUser.setName(user.getName());
            System.out.println("UserSvc: 사용자 정보 수정 -> " + user.getName()
                    + " (ID: " + user.getId().toString().substring(0, 8) + "...)");
            return Optional.of(existingUser);
        } else {
            System.out.println("UserSvc: ID " + user.getId().toString().substring(0, 8) + "... 에 해당하는 사용자 없음. 수정 실패.");
            return Optional.empty(); // 사용자를 찾지 못했음을 알림
        }
    }

    @Override
    public boolean deleteUser(UUID Id) {
        boolean removed = users.removeIf(user -> user.getId().equals(Id)); // ID가 일치하는 사용자 제거
        if (removed) {
            System.out.println("UserSvc: 사용자 ID " + Id.toString().substring(0, 8) + "... 삭제 완료.");
        } else {
            System.out.println("UserSvc: 사용자 ID " + Id.toString().substring(0, 8) + "... 를 찾을 수 없어 삭제 실패.");
        }
        return removed; // 삭제 성공 여부 반환
    }
}
