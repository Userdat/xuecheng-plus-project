package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * @author liuenchen
 * @description TODO
 * @date 2024-05-11 15:17
 */
public class BigFileTest {

    @Test
    //测试文件分块方法
    public void testChunk() throws IOException {
        File sourceFile  = new File("d:/develop/bigfile_test/nacos.mp4");
        //分块文件存储路径
        String chunkPath = "d:/develop/bigfile_test/chunk/";
        File chunkFolder  = new File(chunkPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        //分块大小
        long chunkSize = 1024 * 1024;
        //分块数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        System.out.println("分块总数："+chunkNum);
        //缓冲区大小
        byte[] b = new byte[1024];
        //使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile,"r");
        //分块
        for (int i = 0; i < chunkNum; i++) {
            File file = new File(chunkPath + i);
            if (file.exists()) {
                file.delete();
            }
            boolean newFile = file.createNewFile();
            if (newFile) {
                //向分块文件中写数据
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_read.read(b)) != -1){
                    raf_write.write(b,0,len);
                    if (file.length() >= chunkSize) {
                        break;
                    }
                }

                raf_write.close();
                System.out.println("完成分块"+i);
            }
        }

        raf_read.close();
    }

    @Test
    public void testMerge() throws IOException {
        //块文件目录
        File chunkFolder = new File("d:/develop/bigfile_test/chunk/");
        //原始文件
        File originalFile = new File("d:/develop/bigfile_test/nacos.mp4");
        //合并文件
        File mergeFile = new File("d:/develop/bigfile_test/nacos01.mp4");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //指针指向文件顶端
        raf_write.seek(0);
        //缓冲区
        byte[] b = new byte[1024];
        //分块列表
        File[] fileArray = chunkFolder.listFiles();
        List<File> fileList = Arrays.asList(fileArray);
        // 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            //升序
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        //合并文件
        for (File chunkFile  : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);

            }
            raf_read.close();
        }
        raf_write.close();
        //校验文件
        FileInputStream fileInputStream = new FileInputStream(originalFile);
        FileInputStream mergeFileInputStream = new FileInputStream(mergeFile);
        String originalMd5  = DigestUtils.md5Hex(fileInputStream);
        String mergeFileMd5  = DigestUtils.md5Hex(mergeFileInputStream);
        if (originalMd5.equals(mergeFileMd5)) {
            System.out.println("合并成功");
        }else {
            System.out.println("合并失败");
        }
    }
}
