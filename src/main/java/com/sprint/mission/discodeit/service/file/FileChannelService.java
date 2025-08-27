package com.sprint.mission.discodeit.service.file;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.IOException; // readObject에서 IOException 처리
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileChannelService implements ChannelRepository, Serializable {
    private static final long serialVersionUID = 1L;

    private List<Channel> channels = new ArrayList<>();
    private Long nextId = 1L; // 자동 증가 ID

    public FileChannelService() {
        // 기본 생성자
    }

    // 역직렬화 시 nextId를 재조정
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // 기본 역직렬화 수행
        // 역직렬화 후 nextId를 실제 channels 리스트의 최대 ID + 1로 재조정
        // 이렇게 해야 이전에 저장된 데이터 다음 번호부터 새로운 ID가 부여
        this.nextId = channels.stream()
                .mapToLong(Channel::getId) // 각 Channel 객체에서 ID를 추출하여 Long 스트림 생성
                .max()                      // ID 중 최대값 찾기
                .orElse(0L) + 1;            // 최대값이 없으면 (리스트가 비어있으면) 0L + 1, 즉 1부터 시작
        System.out.println("ChannelSvc: 역직렬화 후 nextId 재설정됨 -> " + nextId);
    }

    @Override
    public Channel createChannel(Channel channel) {
        channel.setId(nextId++); // 현재 nextId 값을 할당하고 1 증가
        channels.add(channel);
        System.out.println("ChannelSvc: 새 채널 등록 -> " + channel.getName()
                + " (ID: " + channel.getId() + ")");
        return channel;
    }

    @Override
    public Optional<Channel> readChannel(Long id) {
        return channels.stream()
                .filter(ch -> ch.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Channel> readAllChannels() {
        return new ArrayList<>(channels);
    }

    @Override
    public Optional<Channel> updateChannel(Channel channel) {
        Optional<Channel> existingChannelOpt = readChannel(channel.getId());
        if (existingChannelOpt.isPresent()) {
            Channel existingChannel = existingChannelOpt.get();
            existingChannel.setName(channel.getName()); // 채널 이름만 수정 가능하다고 가정
            System.out.println("ChannelSvc: 채널 정보 수정 -> " + channel.getName() + " (ID: " + channel.getId() + ")");
            return Optional.of(existingChannel);
        } else {
            System.out.println("ChannelSvc: ID " + channel.getId() + " 에 해당하는 채널 없음. 수정 실패.");
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteChannel(Long Id) {
        boolean removed = channels.removeIf(ch -> ch.getId().equals(Id));
        if (removed) {
            System.out.println("ChannelSvc: 채널 ID " + Id + " 삭제 완료.");
        } else {
            System.out.println("ChannelSvc: 채널 ID " + Id + " 를 찾을 수 없어 삭제 실패.");
        }
        return removed;
    }
}
