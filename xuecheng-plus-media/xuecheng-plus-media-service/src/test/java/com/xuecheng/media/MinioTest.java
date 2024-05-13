package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author liuenchen
 * @description 测试minio上传
 * @date 2024-05-09 17:18
 */
public class MinioTest {

    static MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.101.65:9000")
            .credentials("minioadmin", "minioadmin")
            .build();

    /**
     * 文件上传
     */
    @SneakyThrows
    @Test
    public void test_upload(){

        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".mp4");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }

        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .object("test001.mp4")
                    .object("001/test001.mp4") //添加子目录
                    .filename("D:\\develop\\upload\\1mp4.temp")
                    .contentType(mimeType) //默认根据扩展名确定文件内容类型，也可以指定
                    .build();
            minioClient.uploadObject(testbucket);
            System.out.println("上传成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除文件
     */
    @Test
    public void test_delete(){
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket("testbucket").object("001/test001.mp4").build());
            System.out.println("删除成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  查询文件,从minio下载
     */
    @Test
    public void getFile(){
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("test001.mp4").build();
        try {
            FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
            FileOutputStream outputStream = new FileOutputStream(new File("D:\\develop\\upload\\1_2.mp4"));
            IOUtils.copy(inputStream,outputStream);
            //校验文件的完整性对文件的内容进行md5
            String source_md5 = DigestUtils.md5Hex(inputStream);
            String local_md5 = DigestUtils.md5Hex(new FileInputStream(new File("D:\\develop\\upload\\1_2.mp4")));
            if (source_md5.equals(local_md5)) {
                System.out.println("下载成功");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //将分块文件上传至minio
    @Test
    public void uploadChunk(){
        String chunkFolderPath = "D:\\develop\\upload\\chunk\\";
        File chunkFolder = new File(chunkFolderPath);
        //分块文件
        File[] files = chunkFolder.listFiles();
        //将分块文件上传至minio
        for (int i = 0; i < files.length; i++) {
            try {
                UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder().bucket("testbucket").object("chunk/" + i).filename(files[i].getAbsolutePath()).build();
                minioClient.uploadObject(uploadObjectArgs);
                System.out.println("上传分块成功"+i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    //合并文件，要求分块文件最小5M
    public void test_merge() throws Exception {
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i).limit(6)
                .map(i -> ComposeSource.builder().bucket("testbucket").object("chunk/".concat(Integer.toString(i))).build())
                .collect(Collectors.toList());
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder().bucket("testbucket").object("merge01.mp4").sources(sources).build();
        minioClient.composeObject(composeObjectArgs);

    }

    @Test
    public void test_removeObjects(){
        //合并分块完成将分块文件清除
        List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                .limit(6)
                .map(i -> new DeleteObject("chunk/".concat(Integer.toString(i))))
                .collect(Collectors.toList());

        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("testbucket").objects(deleteObjects).build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        results.forEach(r->{
            DeleteError deleteError = null;
            try {
                deleteError = r.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
