package com.sprint.mission.discodeit.repository.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
<<<<<<< HEAD
<<<<<<< HEAD
import java.util.Optional;
import java.util.UUID;

public class FileEdit {

    // 싱글 톤이면 한곳에 몰리수 있어어 인스턴스로 생성 생각중
=======

public class FileEdit {

>>>>>>> 박지훈
=======

public class FileEdit {

=======
import java.util.Optional;
import java.util.UUID;

public class FileEdit {

    // 싱글 톤이면 한곳에 몰리수 있어어 인스턴스로 생성 생각중
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
    public void init(Path directory){
        //저장할 경로의 파일 초기화
        if(!Files.exists(directory)){
            try{
                Files.createDirectories(directory);
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }
    }

<<<<<<< HEAD
<<<<<<< HEAD
    public <T> void save(Path directory,UUID id, T date){
        Path filePath = directory.resolve(id+".ser");
=======
    public <T> void save(Path filePath, T date){
>>>>>>> 박지훈
=======
    public <T> void save(Path filePath, T date){
=======
    public <T> void save(Path directory,UUID id, T date){
        Path filePath = directory.resolve(id+".ser");
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
        try(FileOutputStream fos = new FileOutputStream(filePath.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
        ){
            oos.writeObject(date);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    };

<<<<<<< HEAD
<<<<<<< HEAD
    //단일 노드 오류 있음
    public <T> Optional<T> load(Path directory, UUID id){
        Path filePath = directory.resolve(id+".ser");
        if(Files.exists(filePath)){
            try(
                    FileInputStream fis = new FileInputStream(filePath.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                T date = (T) ois.readObject();
                return  Optional.of(date);

            }catch (IOException | ClassNotFoundException e){
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    public <T> List<T> loadAll(Path directory){
=======
    public <T> List<T> load(Path directory){
>>>>>>> 박지훈
=======
    public <T> List<T> load(Path directory){
=======
    //단일 노드 오류 있음
    public <T> Optional<T> load(Path directory, UUID id){
        Path filePath = directory.resolve(id+".ser");
        if(Files.exists(filePath)){
            try(
                    FileInputStream fis = new FileInputStream(filePath.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                T date = (T) ois.readObject();
                return  Optional.of(date);

            }catch (IOException | ClassNotFoundException e){
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    public <T> List<T> loadAll(Path directory){
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
        if(Files.exists(directory)){
            try{
                List<T> list = Files.list(directory)
                        .map(path ->{
                            try(FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis);
                            ){
                                Object data = ois.readObject();
                                return (T) data;
                            }catch(IOException | ClassNotFoundException e){
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
                return list;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            return new ArrayList<>();
        }
    };

<<<<<<< HEAD
<<<<<<< HEAD
    public Boolean delete(Path directory, UUID id){
        Path filePath = directory.resolve(id+".ser");
=======
    public Boolean delete(Path filePath){
>>>>>>> 박지훈
=======
    public Boolean delete(Path filePath){
=======
    public Boolean delete(Path directory, UUID id){
        Path filePath = directory.resolve(id+".ser");
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
         return  filePath.toFile().delete();
    }

}
