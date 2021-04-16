package ma.ensaj.geolocation.ui.components;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class InternetCheck extends AsyncTask<Void,Void,Boolean> {
    private Consumer mConsumer;

    public  interface Consumer {
        void accept(Boolean internet);
    }
    public InternetCheck(Consumer consumer) {
        mConsumer = consumer; execute();
    }
    @Override protected Boolean doInBackground(Void... voids) {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            InetAddress address = InetAddress.getByName("google.com");
            SocketAddress socketAddress = new InetSocketAddress(address, 80);

            sock.connect(socketAddress, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }
    @Override protected void onPostExecute(Boolean internet) {
        mConsumer.accept(internet);
    }
}
