import org.junit.jupiter.api.Test;

/**
 * @author: 鵺白
 * @date: 2023/8/12
 * @description:
 */
public class UploadFileTest {
    @Test
    public void test1(){
        String filename = "esse.jpg";
        String suffix = filename.substring(filename.lastIndexOf("."));
        System.out.println(suffix);
    }
}
