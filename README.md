<<<<<<< HEAD
<<<<<<< HEAD
# 기본 요구사항
## Spring 프로젝트 초기화
- [x] Spring Initializr를 통해 zip 파일을 다운로드하세요.
  - 빌드 시스템은 Gradle - Groovy를 사용합니다.
  - 언어는 Java 17를 사용합니다.
  - Spring Boot의 버전은 3.4.0입니다.
  - GroupId는 com.sprint.mission입니다.
  - ArtifactId와 Name은 discodeit입니다.
  - packaging 형식은 Jar입니다
    - Dependency를 추가합니다.
    - Lombok
    - Spring Web
  - zip 파일을 압축해제하고 원래 진행 중이던 프로젝트에 붙여넣기하세요. 일부 파일은 덮어쓰기할 수 있습니다.
  - application.properties 파일을 yaml 형식으로 변경하세요.
  - DiscodeitApplication의 main 메서드를 실행하고 로그를 확인해보세요.
## Bean 선언 및 테스트

- [x] File*Repository 구현체를 Repository 인터페이스의 Bean으로 등록하세요.
- [x] Basic*Service 구현체를 Service 인터페이스의 Bean으로 등록하세요.
- [x] JavaApplication에서 테스트했던 코드를 DiscodeitApplication에서 테스트해보세요.
  - JavaApplication 의 main 메소드를 제외한 모든 메소드를 DiscodeitApplication클래스로 복사하세요.
  - JavaApplication의 main 메소드에서 Service를 초기화하는 코드를 Spring Context를 활용하여 대체하세요.

    ````
    // JavaApplication
    public static void main(String[] args) {
    // 레포지토리 초기화
    // ...
    // 서비스 초기화
    UserService userService = new BasicUserService(userRepository);
    ChannelService channelService = new BasicChannelService(channelRepository);
    MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository);
    
        // ...
    }
    
    // DiscodeitApplication
    public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
    // 서비스 초기화
    // TODO context에서 Bean을 조회하여 각 서비스 구현체 할당 코드 작성하세요.
    UserService userService;
    ChannelService channelService;
    MessageService messageService;
    
        // ...
    }
    ````
  - JavaApplication의 main 메소드의 셋업, 테스트 부분의 코드를 DiscodeitApplication클래스로 복사하세요.
    ````
    public static void main(String[] args) {
    // ...
    // 셋업
    User user = setupUser(userService);
    Channel channel = setupChannel(channelService);
    // 테스트
    messageCreateTest(messageService, channel, user);
    }
    ````
## Spring 핵심 개념 이해하기
- [x] JavaApplication과 DiscodeitApplication에서 Service를 초기화하는 방식의 차이에 대해 다음의 키워드를 중심으로 정리해보세요.
  - IoC Container
  - Dependency Injection
  - Bean
  > 이 내용은 PR에 첨부해주세요.

## Lombok 적용
- [x] 도메인 모델의 getter 메소드를 @Getter로 대체해보세요.
- [x] Basic*Service의 생성자를 @RequiredArgsConstructor로 대체해보세요.
# 비즈니스 로직 고도화
## 시간 타입 변경하기
- [x] 시간을 다루는 필드의 타입은 Instant로 통일합니다.
    - 기존에 사용하던 Long보다 가독성이 뛰어나며, 시간대(Time Zone) 변환과 정밀한 시간 연산이 가능해 확장성이 높습니다.
## 새로운 도메인 추가하기
  - 도메인 모델 간 참조 관계를 참고하세요.
    ```mermaid
        classDiagram
    class User {
        +UUID id
        +UUID profileId
        ...
    }

    class Channel {
        +UUID id
        ...
    }

    class UserStatus {
        +UUID id
        +UUID userId
        ...
    }

    class ReadStatus {
        +UUID id
        +UUID userId
        +UUID channelId
        ...
    }

    class Message {
        +UUID id
        +UUID channelId
        +UUID authorId
        +List<UUID> attachmentIds
        ...
    }

    class BinaryContent {
        +UUID id
        ...
    }

    User "1" --> "0..*" BinaryContent : profileId
    Message "1" --> "0..*" BinaryContent : attachmentIds
    
    UserStatus "1" --> "1" User : userId
    ReadStatus "*" --> "1" User : userId
    Message "*" --> "1" User : authorId
    
    ReadStatus "*" --> "1" Channel : channelId
    Message "*" --> "1" Channel : channelId
    
    ```
- 공통: 앞서 정의한 도메인 모델과 동일하게 공통 필드(id, createdAt, updatedAt)를 포함합니다.

- [x] ReadStatus
    - 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델입니다. 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다.
- [x] UserStatus
    - 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다. 사용자의 온라인 상태를 확인하기 위해 활용합니다.
    - 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드를 정의하세요.
    - 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.
- [x] BinaryContent
  - 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다. 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
  - 수정 불가능한 도메인 모델로 간주합니다. 따라서 updatedAt 필드는 정의하지 않습니다.
  - User, Message 도메인 모델과의 의존 관계 방향성을 잘 고려하여 id 참조 필드를 추가하세요.
- [x]  각 도메인 모델 별 레포지토리 인터페이스를 선언하세요.
  - 레포지토리 구현체(File, JCF)는 아직 구현하지 마세요. 이어지는 서비스 고도화 요구사항에 따라 레포지토리 인터페이스에 메소드가 추가될 수 있어요.
## DTO 활용하기

## UserService 고도화
- 고도화
    - [x] create
      - 선택적으로 프로필 이미지를 같이 등록할 수 있습니다.
      - DTO를 활용해 파라미터를 그룹화합니다.
        - 유저를 등록하기 위해 필요한 파라미터, 프로필 이미지를 등록하기 위해 필요한 파라미터 등
          - username과 email은 다른 유저와 같으면 안됩니다.
      - UserStatus를 같이 생성합니다.
    - [x] find, findAll
      - DTO를 활용하여:
        - 사용자의 온라인 상태 정보를 같이 포함하세요.
        - 패스워드 정보는 제외하세요.
   - [x] update
     - 선택적으로 프로필 이미지를 대체할 수 있습니다.
     - DTO를 활용해 파라미터를 그룹화합니다.
       - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
   - [x] delete
     - 관련된 도메인도 같이 삭제합니다.
       - BinaryContent(프로필), UserStatus
## 의존성
- 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
    ```mermaid
    classDiagram
        UserService --> UserRepository
        UserService --> BinaryContentRepository
        UserService --> UserStatusRepository
    ```

## AuthService 구현
- [x] login
  - username, password과 일치하는 유저가 있는지 확인합니다.
      - 일치하는 유저가 있는 경우: 유저 정보 반환
      - 일치하는 유저가 없는 경우: 예외 발생
  - DTO를 활용해 파라미터를 그룹화합니다.
- 의존성
    - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
      ```mermaid
      classDiagram
          AuthService --> UserRepository
      ```

## ChannelService 고도화
- 고도화
    - [x] create
        - PRIVATE 채널과 PUBLIC 채널을 생성하는 메소드를 분리합니다.
        - 분리된 각각의 메소드를 DTO를 활용해 파라미터를 그룹화합니다.
        - PRIVATE 채널을 생성할 때:
            - 채널에 참여하는 User의 정보를 받아 User 별 ReadStatus 정보를 생성합니다.
            - name과 description 속성은 생략합니다.
        - PUBLIC 채널을 생성할 때에는 기존 로직을 유지합니다.
    - [x] find
        - DTO를 활용하여:
            - 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
            - PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다.
    - [x] findAll
        - DTO를 활용하여:
            - 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
            - PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다.
        - 특정 User가 볼 수 있는 Channel 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findAllByUserId
        - PUBLIC 채널 목록은 전체 조회합니다.
        - PRIVATE 채널은 조회한 User가 참여한 채널만 조회합니다.
    - [x] update
        - DTO를 활용해 파라미터를 그룹화합니다.
            - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
            - PRIVATE 채널은 수정할 수 없습니다.
    - [x] delete 
        - 관련된 도메인도 같이 삭제합니다.
            - Message, ReadStatus
- 의존성
   - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
   ```mermaid
    classDiagram
        ChannelService --> ChannelRepository
        ChannelService --> ReadStatusRepository
        ChannelService --> MessageRepository
   ```
## MessageService 고도화
- 고도화
    - [x] create
        - 선택적으로 여러 개의 첨부파일을 같이 등록할 수 있습니다.
        - DTO를 활용해 파라미터를 그룹화합니다.
    - [x] findAll
        - 특정 Channel의 Message 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findallByChannelId
    - [x] update
        - DTO를 활용해 파라미터를 그룹화합니다.
            - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
    - [x] delete
        - 관련된 도메인도 같이 삭제합니다.
            - 첨부파일(BinaryContent)
- 의존성
    - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
  ```mermaid
  classDiagram
      MessageService --> MessageRepository
      MessageService --> UserRepository
      MessageService --> ChannelRepository
      MessageService --> BinaryContentRepository
   ```

## ReadStatusService 구현
- [x] create
    - DTO를 활용해 파라미터를 그룹화합니다.
    - 관련된 Channel이나 User가 존재하지 않으면 예외를 발생시킵니다.
    - 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
- [x] find
    - id로 조회합니다.
- [x] findAllByUserId
    - userId를 조건으로 조회합니다.
- [x] update
    - DTO를 활용해 파라미터를 그룹화합니다.
    - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
- [x] delete
    - id로 삭제합니다.
- 의존성
    - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
    ```mermaid
  classDiagram
  ReadStatusService --> ReadStatusRepository
  ReadStatusService --> UserRepository
  ReadStatusService --> ChannelRepository
   ```

## UserStatusService 
- 고도화
    - [x] create
        - DTO를 활용해 파라미터를 그룹화합니다.
        - 관련된 User가 존재하지 않으면 예외를 발생시킵니다.
        - 같은 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
    - [x] find
        - id로 조회합니다.
    - [x] findAll
        - 모든 객체를 조회합니다.
    - [x] update
        - DTO를 활용해 파라미터를 그룹화합니다.
        - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
    - [x] updateByUserId
        - userId 로 특정 User의 객체를 업데이트합니다.
    - [x] delete
        - id로 삭제합니다.
- 의존성
    - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
    ```mermaid
    classDiagram
        UserStatusService --> UserStatusRepository
        UserStatusService --> UserRepository
   ```

## BinaryContentService 구현
- [x] create
    - DTO를 활용해 파라미터를 그룹화합니다.
- [x] find
    - id로 조회합니다.
- [x] findAllByIdIn
    - id 목록으로 조회합니다.
- [x] delete
    - id로 삭제합니다.
- 의존성
    - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
    ```mermaid
    classDiagram
        BinaryContentService --> BinaryContentRepository
   ```    

## 새로운 도메인 Repository 구현체 구현
- [x] 지금까지 인터페이스로 설계한 각각의 Repository를 JCF, File로 각각 구현하세요.
    ```mermaid
    classDiagram
        class FileRepository
        class JCFRepository
        class Repository{ <<interface>> }     
        FileRepository --|> Repository
        JCFRepository --|> Repository
   ```   

# 심화 요구사항
## Bean 다루기
- [x]  Repository 구현체 중에 어떤 구현체를 Bean으로 등록할지 Java 코드의 변경 없이 application.yaml 설정 값을 통해 제어해보세요.
    ```
        #application.yaml
        discodeit:
        repository:
        type: jcf   # jcf | file
    ```
   - [x] discodeit.repository.type 설정값에 따라 Repository 구현체가 정해집니다.
     - [x] 값이 jcf 이거나 없으면 JCF*Repository 구현체가 Bean으로 등록되어야 합니다.
     - [x] 값이 file 이면 File*Repository 구현체가 Bean으로 등록되어야 합니다.
- [x]  File*Repository 구현체의 파일을 저장할 디렉토리 경로를 application.yaml 설정 값을 통해 제어해보세요.
    ```
    # application.yaml
    discodeit:
    repository:
    type: jcf   # jcf | file
    file-directory: .discodeit
    ```
=======
=======
>>>>>>> 박지훈
# 1차 기본 요구사항
## 프로젝트 초기화
- [x] IntelliJ를 통해 다음의 조건으로 Java 프로젝트를 생성합니다.
    - [x]  IntelliJ에서 제공하는 프로젝트 템플릿 중 Java를 선택합니다.
    - [x]  프로젝트의 경로는 스프린트 미션 리포지토리의 경로와 같게 설정합니다.
        - 예를 들어 스프린트 미션 리포지토리의 경로가 /some/path/1-sprint-mission 이라면:
          - Name은 1-sprint-mission
          - Location은 /some/path
          - 으로 설정합니다.

- [x]  Create Git Repository 옵션은 체크하지 않습니다.

- [x]  Build system은 Gradle을 사용합니다. Gradle DSL은 Groovy를 사용합니다.

- [x]  JDK 17을 선택합니다.

- [x]  GroupId는 com.sprint.mission로 설정합니다.

- [x]  ArtifactId는 수정하지 않습니다.

- [x]  .gitignore에 IntelliJ와 관련된 파일이 형상관리 되지 않도록 .idea디렉토리를 추가합니다.


## 도메인 모델링
- [x] 디스코드 서비스를 활용해보면서 각 도메인 모델에 필요한 정보를 도출하고, Java Class로 구현하세요.
    - 패키지명: com.sprint.mission.discodeit.entity
    - [x] 도메인 모델 정의
      - 공통
        - id: 객체를 식별하기 위한 id로 UUID 타입으로 선언합니다.
        - createdAt, updatedAt: 각각 객체의 생성, 수정 시간을 유닉스 타임스탬프로 나타내기 위한 필드로 Long 타입으로 선언합니다.
        - [x] User
            - id(UUID)
            - 이름(String)
            - 나이(int)
            - 이메일
            - 채널 목록(List<Channel>)
            - 친구 목록(List<User>)
            - 생성 일자(Long)
            - 수정 일자(Long)
        - [x] Channel
            - id (UUID)
            - 채널이름 (String)
            - 방장 (User)
            - User 목록 (List<User>)
            - 메세지 목록 (List<Message>)
            - 생성 일자 (Long)
            - 수정 일자 (Long)
        - [x] Message
            - id (UUID)
            - 보낸 User (User)
            - 메세지 본문( String)
            - 생성 일자 (Long)
            - 수정 일자 (Long)
    - 생성자
      - [x] id는 생성자에서 초기화하세요.
      - [x] createdAt는 생성자에서 초기화하세요.
      - [x] id, createdAt, updatedAt을 제외한 필드는 생성자의 파라미터를 통해 초기화하세요.

    - 메소드
      - [x] 각 필드를 반환하는 Getter 함수를 정의하세요.
      - [x] 필드를 수정하는 update 함수를 정의하세요.


## 서비스 설계 및 구현
- [x] 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언하세요.
  - 인터페이스 패키지명: com.sprint.mission.discodeit.service
  - 인터페이스 네이밍 규칙: [도메인 모델 이름]Service
- [x] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
  - 클래스 패키지명: com.sprint.mission.discodeit.service.jcf
  - 클래스 네이밍 규칙: JCF[인터페이스 이름]
  - Java Collections Framework를 활용하여 데이터를 저장할 수 있는 필드(data)를 final로 선언하고 생성자에서 초기화하세요.
  - data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.

## 메인 클래스 구현
- [x] 메인 메소드가 선언된 JavaApplication 클래스를 선언하고, 도메인 별 서비스 구현체를 테스트해보세요.
  - 등록
  - 조회(단건, 다건)
  - 수정
  - 수정된 데이터 조회
  - 삭제
  - 조회를 통해 삭제되었는지 확인

## 심화 요구 사항
### 서비스 간 의존성 주입
- [X] 도메인 모델 간 관계를 고려해서 검증하는 로직을 추가하고, 테스트해보세요.
    - 힌트: Message를 생성할 때 연관된 도메인 모델 데이터 확인하기


# 2차 요구사항
## 기본 요구사항
### File IO를 통한 데이터 영속화
- [x] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.

    - 클래스 패키지명: com.sprint.mission.discodeit.service.file

    - 클래스 네이밍 규칙: File[인터페이스 이름]

    - JCF 대신 FileIO와 객체 직렬화를 활용해 메소드를 구현하세요.

    - Application에서 서비스 구현체를 File*Service로 바꾸어 테스트해보세요.

### 서비스 구현체 분석
- [x] JCF*Service 구현체와 File*Service 구현체를 비교하여 공통점과 차이점을 발견해보세요.
    - "비즈니스 로직"과 관련된 코드를 식별해보세요.
    - "저장 로직"과 관련된 코드를 식별해보세요.

| 구분             | JCF 서비스 구현체                                            | 파일 서비스 구현체                             |
| -------------- | ------------------------------------------------------ | -------------------------------------- |
| **데이터 저장 위치**  | 메모리 (In-Memory)                                        | 디스크 (File System)                      |
| **데이터 영속성**    | 애플리케이션 종료 시 데이터 소멸(휘발성)                                | 애플리케이션 종료 후에도<br>데이터 유지(비 휘발성)         |
| **처리 속도**      | 매우 빠름 (디스크 I/O 없음)                                     | 상대적으로 느림 (디스크 I/O 발생)                  |
| **주요 용도**      | 임시 데이터 캐싱, 세션 관리, <br>빠른 조회가 필요한 데이터 처리                | 데이터 영구 저장, 로그 기록,<br>설정 정보 관리          |
| **데이터 동시성 제어** | 동기화(synchronized) 컬렉션 또는 <br/>`java.util.concurrent` 패키지 필요 | 파일 잠금(File Locking) 등 운영체제 수준의 메커니즘 필요 |
| **확장성**        | 단일 애플리케이션 메모리에 종속적                                     | 분산 파일 시스템 등을 통해 확장 가능                  |
---
### 레포지토리 설계 및 구현
- [x] "저장 로직"과 관련된 기능을 도메인 모델 별 인터페이스로 선언하세요.
  - 인터페이스 패키지명: com.sprint.mission.discodeit.repository
  - 인터페이스 네이밍 규칙: [도메인 모델 이름]Repository
- [x] 다음의 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요.
  - 클래스 패키지명: com.sprint.mission.discodeit.repository.jcf
  - 클래스 네이밍 규칙: JCF[인터페이스 이름]
  - 기존에 구현한 JCF*Service 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.
- [x] 다음의 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요.
  - 클래스 패키지명: com.sprint.mission.discodeit.repository.file
  - 클래스 네이밍 규칙: File[인터페이스 이름]
  - 기존에 구현한 File*Service 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.
## 심화 요구 사항
### 관심사 분리를 통한 레이어 간 의존성 주입
- [x] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
  - 클래스 패키지명: com.sprint.mission.discodeit.service.basic
  - 클래스 네이밍 규칙: Basic[인터페이스 이름]
  - 기존에 구현한 서비스 구현체의 "비즈니스 로직"과 관련된 코드를 참고하여 구현하세요.
  - 필요한 Repository 인터페이스를 필드로 선언하고 생성자를 통해 초기화하세요.
  - "저장 로직"은 Repository 인터페이스 필드를 활용하세요. (직접 구현하지 마세요.)
- [x] Basic*Service 구현체를 활용하여 테스트해보세요.
  - 코드 템플릿
    ````
    public class JavaApplication {
        static User setupUser(UserService userService) {
            User user = userService.create("woody", "woody@codeit.com", "woody1234");
            return user;
        }
    
        static Channel setupChannel(ChannelService channelService) {
            Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
            return channel;
        }
    
        static void messageCreateTest(MessageService messageService, Channel channel, User author) {
            Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
            System.out.println("메시지 생성: " + message.getId());
        }
    
        public static void main(String[] args) {
            // 서비스 초기화
            // TODO Basic*Service 구현체를 초기화하세요.
            UserService userService;
            ChannelService channelService;
            MessageService messageService;
    
            // 셋업
            User user = setupUser(userService);
            Channel channel = setupChannel(channelService);
            // 테스트
            messageCreateTest(messageService, channel, user);
        }
    }
    ````
  - [x] JCF*Repository  구현체를 활용하여 테스트해보세요.
  - [x] File*Repository 구현체를 활용하여 테스트해보세요.
- [x] 이전에 작성했던 코드(JCF*Service 또는 File*Service)와 비교해 어떤 차이가 있는지 정리해보세요.
   
    | 항목             | 이전 코드 (JCF*Service / File*Service)                         | 개선된 코드 (Basic*Service + Repository)                              |
    |----------------|------------------------------------------------------------|------------------------------------------------------------------|
    | **핵심 역할 및 책임** | **All-in-One 구조**<br>서비스가 비즈니스 규칙과 데이터 저장을 모두 처리           | **명확한 역할 분리**<br>서비스는 비즈니스 로직, 레포지토리는 데이터 저장을 전담                 |
    | **결합도**        | **강한 결합**<br>비즈니스 로직이 특정 저장 기술에 직접적으로 종속됨 | **느슨한 결합**<br>서비스가 실제 저장 기술이 아닌 인터페이스(추상화)에 의존 |
    | **의존성 주입**     | **사용하지 않음**<br>서비스가 데이터 저장소를 직접 생성하여 관리                    | **적극적으로 사용**<br>외부에서 생성된 레포지토리 구현체를 주입받아 사용                      |
    | **유연성 및 확장성**  | **낮음**<br>저장 방식을 변경하려면 서비스 코드 수정이 필수적임                     | **높음**<br>서비스 코드 수정 없이 레포지토리 교체만으로 기능 확장                         |
    | **테스트 용이성**    | **어려움**<br>외부 시스템(파일 등)에 의존하여 독립적인 테스트가 복잡                 | **쉬움**<br>가짜 레포지토리를 주입하여 서비스 로직만 신속하게 테스트                  |
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
# 기본 요구사항
## Spring 프로젝트 초기화
- [x] Spring Initializr를 통해 zip 파일을 다운로드하세요.
  - 빌드 시스템은 Gradle - Groovy를 사용합니다.
  - 언어는 Java 17를 사용합니다.
  - Spring Boot의 버전은 3.4.0입니다.
  - GroupId는 com.sprint.mission입니다.
  - ArtifactId와 Name은 discodeit입니다.
  - packaging 형식은 Jar입니다
    - Dependency를 추가합니다.
    - Lombok
    - Spring Web
  - zip 파일을 압축해제하고 원래 진행 중이던 프로젝트에 붙여넣기하세요. 일부 파일은 덮어쓰기할 수 있습니다.
  - application.properties 파일을 yaml 형식으로 변경하세요.
  - DiscodeitApplication의 main 메서드를 실행하고 로그를 확인해보세요.
## Bean 선언 및 테스트

- [x] File*Repository 구현체를 Repository 인터페이스의 Bean으로 등록하세요.
- [x] Basic*Service 구현체를 Service 인터페이스의 Bean으로 등록하세요.
- [x] JavaApplication에서 테스트했던 코드를 DiscodeitApplication에서 테스트해보세요.
  - JavaApplication 의 main 메소드를 제외한 모든 메소드를 DiscodeitApplication클래스로 복사하세요.
  - JavaApplication의 main 메소드에서 Service를 초기화하는 코드를 Spring Context를 활용하여 대체하세요.

    ````
    // JavaApplication
    public static void main(String[] args) {
    // 레포지토리 초기화
    // ...
    // 서비스 초기화
    UserService userService = new BasicUserService(userRepository);
    ChannelService channelService = new BasicChannelService(channelRepository);
    MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository);
    
        // ...
    }
    
    // DiscodeitApplication
    public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
    // 서비스 초기화
    // TODO context에서 Bean을 조회하여 각 서비스 구현체 할당 코드 작성하세요.
    UserService userService;
    ChannelService channelService;
    MessageService messageService;
    
        // ...
    }
    ````
  - JavaApplication의 main 메소드의 셋업, 테스트 부분의 코드를 DiscodeitApplication클래스로 복사하세요.
    ````
    public static void main(String[] args) {
    // ...
    // 셋업
    User user = setupUser(userService);
    Channel channel = setupChannel(channelService);
    // 테스트
    messageCreateTest(messageService, channel, user);
    }
    ````
## Spring 핵심 개념 이해하기
- [x] JavaApplication과 DiscodeitApplication에서 Service를 초기화하는 방식의 차이에 대해 다음의 키워드를 중심으로 정리해보세요.
  - IoC Container
  - Dependency Injection
  - Bean
  > 이 내용은 PR에 첨부해주세요.

## Lombok 적용
- [x] 도메인 모델의 getter 메소드를 @Getter로 대체해보세요.
- [x] Basic*Service의 생성자를 @RequiredArgsConstructor로 대체해보세요.
# 비즈니스 로직 고도화
## 시간 타입 변경하기
- [x] 시간을 다루는 필드의 타입은 Instant로 통일합니다.
    - 기존에 사용하던 Long보다 가독성이 뛰어나며, 시간대(Time Zone) 변환과 정밀한 시간 연산이 가능해 확장성이 높습니다.
## 새로운 도메인 추가하기
  - 도메인 모델 간 참조 관계를 참고하세요.
    ```mermaid
        classDiagram
    class User {
        +UUID id
        +UUID profileId
        ...
    }

    class Channel {
        +UUID id
        ...
    }

    class UserStatus {
        +UUID id
        +UUID userId
        ...
    }

    class ReadStatus {
        +UUID id
        +UUID userId
        +UUID channelId
        ...
    }

    class Message {
        +UUID id
        +UUID channelId
        +UUID authorId
        +List<UUID> attachmentIds
        ...
    }

    class BinaryContent {
        +UUID id
        ...
    }

    User "1" --> "0..*" BinaryContent : profileId
    Message "1" --> "0..*" BinaryContent : attachmentIds
    
    UserStatus "1" --> "1" User : userId
    ReadStatus "*" --> "1" User : userId
    Message "*" --> "1" User : authorId
    
    ReadStatus "*" --> "1" Channel : channelId
    Message "*" --> "1" Channel : channelId
    
    ```
- 공통: 앞서 정의한 도메인 모델과 동일하게 공통 필드(id, createdAt, updatedAt)를 포함합니다.

- [x] ReadStatus
    - 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델입니다. 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다.
- [x] UserStatus
    - 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다. 사용자의 온라인 상태를 확인하기 위해 활용합니다.
    - 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드를 정의하세요.
    - 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.
- [x] BinaryContent
  - 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다. 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
  - 수정 불가능한 도메인 모델로 간주합니다. 따라서 updatedAt 필드는 정의하지 않습니다.
  - User, Message 도메인 모델과의 의존 관계 방향성을 잘 고려하여 id 참조 필드를 추가하세요.
- [x]  각 도메인 모델 별 레포지토리 인터페이스를 선언하세요.
  - 레포지토리 구현체(File, JCF)는 아직 구현하지 마세요. 이어지는 서비스 고도화 요구사항에 따라 레포지토리 인터페이스에 메소드가 추가될 수 있어요.
## DTO 활용하기

## UserService 고도화
- 고도화
    - [x] create
      - 선택적으로 프로필 이미지를 같이 등록할 수 있습니다.
      - DTO를 활용해 파라미터를 그룹화합니다.
        - 유저를 등록하기 위해 필요한 파라미터, 프로필 이미지를 등록하기 위해 필요한 파라미터 등
          - username과 email은 다른 유저와 같으면 안됩니다.
      - UserStatus를 같이 생성합니다.
    - [x] find, findAll
      - DTO를 활용하여:
        - 사용자의 온라인 상태 정보를 같이 포함하세요.
        - 패스워드 정보는 제외하세요.
   - [x] update
     - 선택적으로 프로필 이미지를 대체할 수 있습니다.
     - DTO를 활용해 파라미터를 그룹화합니다.
       - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
   - [x] delete
     - 관련된 도메인도 같이 삭제합니다.
       - BinaryContent(프로필), UserStatus
## 의존성
- 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
    ```mermaid
    classDiagram
        UserService --> UserRepository
        UserService --> BinaryContentRepository
        UserService --> UserStatusRepository
    ```

## AuthService 구현
- [x] login
  - username, password과 일치하는 유저가 있는지 확인합니다.
      - 일치하는 유저가 있는 경우: 유저 정보 반환
      - 일치하는 유저가 없는 경우: 예외 발생
  - DTO를 활용해 파라미터를 그룹화합니다.
- 의존성
    - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
      ```mermaid
      classDiagram
          AuthService --> UserRepository
      ```

## ChannelService 고도화
- 고도화
    - [x] create
        - PRIVATE 채널과 PUBLIC 채널을 생성하는 메소드를 분리합니다.
        - 분리된 각각의 메소드를 DTO를 활용해 파라미터를 그룹화합니다.
        - PRIVATE 채널을 생성할 때:
            - 채널에 참여하는 User의 정보를 받아 User 별 ReadStatus 정보를 생성합니다.
            - name과 description 속성은 생략합니다.
        - PUBLIC 채널을 생성할 때에는 기존 로직을 유지합니다.
    - [x] find
        - DTO를 활용하여:
            - 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
            - PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다.
    - [x] findAll
        - DTO를 활용하여:
            - 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
            - PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다.
        - 특정 User가 볼 수 있는 Channel 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findAllByUserId
        - PUBLIC 채널 목록은 전체 조회합니다.
        - PRIVATE 채널은 조회한 User가 참여한 채널만 조회합니다.
    - [x] update
        - DTO를 활용해 파라미터를 그룹화합니다.
            - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
            - PRIVATE 채널은 수정할 수 없습니다.
    - [x] delete 
        - 관련된 도메인도 같이 삭제합니다.
            - Message, ReadStatus
- 의존성
   - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
   ```mermaid
    classDiagram
        ChannelService --> ChannelRepository
        ChannelService --> ReadStatusRepository
        ChannelService --> MessageRepository
   ```
## MessageService 고도화
- 고도화
    - [x] create
        - 선택적으로 여러 개의 첨부파일을 같이 등록할 수 있습니다.
        - DTO를 활용해 파라미터를 그룹화합니다.
    - [x] findAll
        - 특정 Channel의 Message 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findallByChannelId
    - [x] update
        - DTO를 활용해 파라미터를 그룹화합니다.
            - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
    - [x] delete
        - 관련된 도메인도 같이 삭제합니다.
            - 첨부파일(BinaryContent)
- 의존성
    - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
  ```mermaid
  classDiagram
      MessageService --> MessageRepository
      MessageService --> UserRepository
      MessageService --> ChannelRepository
      MessageService --> BinaryContentRepository
   ```

## ReadStatusService 구현
- [x] create
    - DTO를 활용해 파라미터를 그룹화합니다.
    - 관련된 Channel이나 User가 존재하지 않으면 예외를 발생시킵니다.
    - 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
- [x] find
    - id로 조회합니다.
- [x] findAllByUserId
    - userId를 조건으로 조회합니다.
- [x] update
    - DTO를 활용해 파라미터를 그룹화합니다.
    - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
- [x] delete
    - id로 삭제합니다.
- 의존성
    - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
    ```mermaid
  classDiagram
  ReadStatusService --> ReadStatusRepository
  ReadStatusService --> UserRepository
  ReadStatusService --> ChannelRepository
   ```

## UserStatusService 
- 고도화
    - [x] create
        - DTO를 활용해 파라미터를 그룹화합니다.
        - 관련된 User가 존재하지 않으면 예외를 발생시킵니다.
        - 같은 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
    - [x] find
        - id로 조회합니다.
    - [x] findAll
        - 모든 객체를 조회합니다.
    - [x] update
        - DTO를 활용해 파라미터를 그룹화합니다.
        - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
    - [x] updateByUserId
        - userId 로 특정 User의 객체를 업데이트합니다.
    - [x] delete
        - id로 삭제합니다.
- 의존성
    - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
    ```mermaid
    classDiagram
        UserStatusService --> UserStatusRepository
        UserStatusService --> UserRepository
   ```

## BinaryContentService 구현
- [x] create
    - DTO를 활용해 파라미터를 그룹화합니다.
- [x] find
    - id로 조회합니다.
- [x] findAllByIdIn
    - id 목록으로 조회합니다.
- [x] delete
    - id로 삭제합니다.
- 의존성
    - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
    ```mermaid
    classDiagram
        BinaryContentService --> BinaryContentRepository
   ```    

## 새로운 도메인 Repository 구현체 구현
- [x] 지금까지 인터페이스로 설계한 각각의 Repository를 JCF, File로 각각 구현하세요.
    ```mermaid
    classDiagram
        class FileRepository
        class JCFRepository
        class Repository{ <<interface>> }     
        FileRepository --|> Repository
        JCFRepository --|> Repository
   ```   

# 심화 요구사항
## Bean 다루기
- [x]  Repository 구현체 중에 어떤 구현체를 Bean으로 등록할지 Java 코드의 변경 없이 application.yaml 설정 값을 통해 제어해보세요.
    ```
        #application.yaml
        discodeit:
        repository:
        type: jcf   # jcf | file
    ```
   - [x] discodeit.repository.type 설정값에 따라 Repository 구현체가 정해집니다.
     - [x] 값이 jcf 이거나 없으면 JCF*Repository 구현체가 Bean으로 등록되어야 합니다.
     - [x] 값이 file 이면 File*Repository 구현체가 Bean으로 등록되어야 합니다.
- [x]  File*Repository 구현체의 파일을 저장할 디렉토리 경로를 application.yaml 설정 값을 통해 제어해보세요.
    ```
    # application.yaml
    discodeit:
    repository:
    type: jcf   # jcf | file
    file-directory: .discodeit
    ```
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
