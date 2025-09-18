package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
<<<<<<< HEAD
<<<<<<< HEAD
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
=======
>>>>>>> 박지훈
=======
=======
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
<<<<<<< HEAD
<<<<<<< HEAD
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileChannelRepository implements ChannelRepository {
    private final Path directory = Paths.get("./src/main/resources/ChannelDate");
    private final FileEdit instance = new  FileEdit();
=======
=======
>>>>>>> 박지훈
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private static final Path directory = Paths.get("./src/main/resources/ChannelDate");
    private static final FileEdit instance = new  FileEdit();

    private Path filePaths(Channel channel) {
        return directory.resolve(channel.getChannelId().toString() + ".ser");
    }
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileChannelRepository implements ChannelRepository {
    private final Path directory = Paths.get("./src/main/resources/ChannelDate");
    private final FileEdit instance = new  FileEdit();
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈

    public FileChannelRepository(){
        instance.init(directory);
    }

<<<<<<< HEAD
<<<<<<< HEAD
    @Override
    public Channel save(Channel channel) {
        instance.save(directory,channel.getId(), channel);
        return channel;
    }
    @Override
    public Optional<Channel> findById(UUID id) {return instance.load(directory,id);}

    @Override
    public List<Channel> findAll() {
        return instance.loadAll(directory);
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        boolean isDelete = instance.delete(directory,id);
        if(!isDelete){
            throw new NullPointerException(" 유저 삭제 실패");
        }
    }

=======
=======
>>>>>>> 박지훈
    public void createChannel(Channel channel) {
        instance.save(filePaths(channel), channel);
    }

    public Channel findChannelById(UUID id) {
        List<Channel> channelList = instance.load(directory);
        return channelList.stream()
                .filter(channel -> channel.getChannelId().equals(id))
                .findAny().orElse(null);
    }

    public List<Channel> findAllChannels() {
        return instance.load(directory);
    }

    public void updateChannel(Channel channel) {
        instance.save(filePaths(channel), channel);
    }

    public void deleteChannel(UUID id) {
        Channel channel = findChannelById(id);
        boolean isDelete = instance.delete(filePaths(channel));
        if(!isDelete){
            throw new NullPointerException(channel.getChannelId()+" 유저 삭제 완료");
        }
    }

    public void addMessageToChannel(Channel channel, Message message) {

    }

    public void removeMessageFromChannel(Channel channel, Message message) {

    }
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
    @Override
    public Channel save(Channel channel) {
        instance.save(directory,channel.getId(), channel);
        return channel;
    }
    @Override
    public Optional<Channel> findById(UUID id) {return instance.load(directory,id);}

    @Override
    public List<Channel> findAll() {
        return instance.loadAll(directory);
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        boolean isDelete = instance.delete(directory,id);
        if(!isDelete){
            throw new NullPointerException(" 유저 삭제 실패");
        }
    }

>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
}
