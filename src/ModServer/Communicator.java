package ModServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @implNote 用于和游戏中的Mod进行连接
 */
public class Communicator implements Runnable {
    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(9999);
            Socket client = server.accept();

            // 获取输入流和输出流
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream();

            while (true) {
                String request = in.readLine();
                if (request.equals("test")) {
                    String response = "Testing!!!\n";
                    out.write(response.getBytes());
                    out.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
