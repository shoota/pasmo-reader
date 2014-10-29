package org.anaguma.pasmoreader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.anaguma.pasmoreader.pasmo.data.History;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "::onCreate -----------------------------------------");
        // 表示領域
        TextView txt1 = (TextView)findViewById(R.id.text1);

        // NFC(Felica) ID を取得
        byte[] felicaIDm;
        Intent intent = getIntent();
		Tag nfcTag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if(nfcTag != null) {
			felicaIDm = nfcTag.getId();
		}else {
			Log.d(TAG, "did not get Intent TAG");
			return;
		}

		NfcF felica = NfcF.get(nfcTag);

		try {
			felica.connect();
			byte[] req = readWithoutEncryption(felicaIDm, 10);
			Log.d(TAG, "req:"+toHex(req));

            byte[] res = felica.transceive(req);
            Log.d(TAG, "res:"+toHex(res));
            felica.close();

            txt1.setText(parsePasmoHistory(res));

		} catch (Exception e) {
			// TODO: handle exception
		}




    }





	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private byte[] readWithoutEncryption(byte[] idm, int size)
            throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(100);

        bout.write(0);           // data length. change after all byte set.
        bout.write(0x06);        // Felica command, Read Without Encryption
        bout.write(idm);         // NFC ID (8byte)
        bout.write(1);           // service code length (2byte)
        bout.write(0x0f);        // low byte of service code for pasmo history (little endian)
        bout.write(0x09);        // high byte of service code for pasmo history (little endian)
        bout.write(size);        // number of block. (=< 15)
        for (int i = 0; i < size; i++) {
            bout.write(0x80);    // ブロックエレメント上位バイト 「Felicaユーザマニュアル抜粋」の4.3項参照
            bout.write(i);       // ブロック番号
        }

        byte[] msg = bout.toByteArray();
        msg[0] = (byte) msg.length; // 先頭１バイトはデータ長
        return msg;
    }


    private String toHex(byte[] id) {
        StringBuilder sbuf = new StringBuilder();
        for (int i = 0; i < id.length; i++) {
            String hex = "0" + Integer.toString((int) id[i] & 0x0ff, 16);
            if (hex.length() > 2)
                hex = hex.substring(1, 3);
            sbuf.append(" " + i + ":" + hex);
        }
        return sbuf.toString();
    }

    private String parsePasmoHistory(byte[] res) throws Exception {
        // res[0] = データ長
        // res[1] = 0x07
        // res[2〜9] = カードID
        // res[10,11] = エラーコード。0=正常。
        if (res[10] != 0x00) throw new RuntimeException("Felica error.");

        // res[12] = 応答ブロック数
        // res[13+n*16] = 履歴データ。16byte/ブロックの繰り返し。
        int size = res[12];
        String str = "";
        for (int i = 0; i < size; i++) {
            // 個々の履歴の解析。
            History history = History.parse(res, 13 + i * 16);
            str += history.toString() +"\n";
        }
        return str;
    }
}
