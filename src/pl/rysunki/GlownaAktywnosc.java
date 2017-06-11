package pl.rysunki;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.Calendar;
//Pakiety potrzebne dla OnClickListenera (obs�ugi przycisk�w na g�rze ekranu)
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class GlownaAktywnosc extends Activity implements OnClickListener {

	//Elementy klasy
	private WidokRysowanie widokRys; //Referencja do widoku rysowania
	private ImageButton buttonAktualnyKolor; //Wybrany aktualnie kolor z palety
	private ImageButton buttonRozmiarPedzla;
	private ImageButton buttonNowaKartka; //wskazuje na przycisk do tworzenia "nowej kartki w bloku"
	private ImageButton buttonZapis; //wskazuje na przycisk do zapisu rysunk�w
	private float rozmiarMalyPedzel, rozmiarSredniPedzel, rozmiarDuzyPedzel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.glowna_aktywnosc);
		
		//Przypisz polom klasy referencje do 
		widokRys = (WidokRysowanie)findViewById(R.id.widok_rys);
		
		//Przypisz warto�ci polom klasy
		rozmiarMalyPedzel 	 = getResources().getInteger(R.integer.maly_rozmiar);
		rozmiarSredniPedzel = getResources().getInteger(R.integer.sredni_rozmiar);
		rozmiarDuzyPedzel   = getResources().getInteger(R.integer.duzy_rozmiar);
				
		//Przydziel rozmiar p�dzla
		widokRys.zastosujRozmiarPedzla(rozmiarSredniPedzel);
		
		//pobierz referencj� na palet� kolor�w
		LinearLayout layout_rys = (LinearLayout)findViewById(R.id.paleta_kolorow);
		//Przypisz aktualnej farbie pierwszy kolor z palety
		buttonAktualnyKolor = (ImageButton)layout_rys.getChildAt(0);
		//Zaznacz graficznie, �e kolor jest aktualnie wybrany
		buttonAktualnyKolor.setImageDrawable(getResources().getDrawable(R.drawable.przycisk_wybrany));

		//Zastosuj kolor pierwszy w palecie
		String kolor = buttonAktualnyKolor.getTag().toString();
		widokRys.zastosujKolor(kolor);
		
		//Zapisz referencj� do przycisku zmieniaj�cego rozmiar p�dzla
		buttonRozmiarPedzla = (ImageButton)findViewById(R.id.button_rozmiar_pedzla);
		buttonRozmiarPedzla.setOnClickListener(this); //przydziel listenera
		
		//Zapisz referencj� do przycisku "nowa kartka"
		buttonNowaKartka = (ImageButton)findViewById(R.id.button_nowa_kartka);
		buttonNowaKartka.setOnClickListener(this);
		
		//Zapisz referencj� do przycisku "Zapisz"
		buttonZapis = (ImageButton)findViewById(R.id.button_zapisz);
		buttonZapis.setOnClickListener(this);
	}

	private void pokazOknoRozmiarPedzla() {
		//Klikni�to przycisk z p�dzlem. 
		
		//Wy�wietl okno dialogowe
		final Dialog pedzelDialog = new Dialog(this);
		pedzelDialog.setTitle("Rozmiar p�dzla");
		//Za�aduj layout
		pedzelDialog.setContentView(R.layout.wybor_pedzla);
		
		//Zapisz referencj� do przycisku ma�ego rozmiar p�dzla
		ImageButton button_maly = (ImageButton)pedzelDialog.findViewById(R.id.maly_pedzel);
		button_maly.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v) {
	
		        widokRys.zastosujRozmiarPedzla(rozmiarMalyPedzel);
		        widokRys.zapiszPopRozmiarPedzla(rozmiarMalyPedzel);
		        pedzelDialog.dismiss();
		    }
		});
		
		//U�ytkownik wybra� �redni rozmiar p�dzla
		ImageButton button_sredni = (ImageButton)pedzelDialog.findViewById(R.id.sredni_pedzel);
		button_sredni.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v) {
		    	
		        widokRys.zastosujRozmiarPedzla(rozmiarSredniPedzel);
		        widokRys.zapiszPopRozmiarPedzla(rozmiarSredniPedzel);
		        pedzelDialog.dismiss();
		    }
		});
		 
		//U�ytkownik wybra� du�y rozmiar p�dzla
		ImageButton button_duzy = (ImageButton)pedzelDialog.findViewById(R.id.duzy_pedzel);
		button_duzy.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v) {
		    	//widokRys.setErase(false);
		        widokRys.zastosujRozmiarPedzla(rozmiarDuzyPedzel);
		        widokRys.zapiszPopRozmiarPedzla(rozmiarDuzyPedzel);
		        pedzelDialog.dismiss();
		    }
		});
		//Poka� okno dialogowe z 3 przyciskami
		pedzelDialog.show();
	}
	
	private void pokazOknoNowaKartka() {
		//u�ytkownik chce rozpocz�� nowy rysunek
		
		//Stw�rz okno dialogowe Tak/Anuluj
		AlertDialog.Builder nowaKartkaDialog = new AlertDialog.Builder(this);
		nowaKartkaDialog.setTitle("Nowy rysunek");
		nowaKartkaDialog.setMessage("Rozpocz�� nowy rysunek (obecny zostanie utracony)?");
		nowaKartkaDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener(){
		    public void onClick(DialogInterface dialog, int which){
		        widokRys.nowaKartka();
		        dialog.dismiss();
		    }
		});
		nowaKartkaDialog.setNegativeButton("Anuluj", new DialogInterface.OnClickListener(){
		    public void onClick(DialogInterface dialog, int which){
		        dialog.cancel();
		    }
		});
		nowaKartkaDialog.show();

	}
	private String probaZapisuObrazu(){
		
		//Umo�liw tworzenie obrazu, rezerwuj pami�� cache
    	widokRys.setDrawingCacheEnabled(true); 
    	
    	//Zapisz rysunek jako obraz w galerii
    	String urlObrazu = MediaStore.Images.Media.insertImage(
    			getContentResolver(), widokRys.getDrawingCache(),
    			Calendar.getInstance().getTime().toString()+".png", "Rysunek");
    	
    	//Zwolnij pami�� cache wyk. podczas zapisu
    	widokRys.destroyDrawingCache(); 
    	
    	return urlObrazu;	//Zwr�� URL obrazu
	}
	
	private void pokazOknoZapisu() {
		//U�ytkownik chce zapisa� obecny rysunek
		
		//Stw�rz okno dialogowe Tak/Anuluj
		AlertDialog.Builder zapiszDialog = new AlertDialog.Builder(this);
		zapiszDialog.setTitle("Zapis rysunku");
		zapiszDialog.setMessage("Czy zapisa� rysunek do Galerii urz�dzenia?");
		//Dodaj przycisk "Tak"
		zapiszDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener(){
		    public void onClick(DialogInterface dialog, int which){
		        
		    	String urlObrazu = probaZapisuObrazu();
		    	//Sprawd� wynik operacji zapisu i wy�wietl komunikat
		    	if(urlObrazu == null){
		    		Toast nieZapisanoToast = Toast.makeText(getApplicationContext(), 
			    	        		"B��d! Rysunek nie mo�e by� zapisany.", Toast.LENGTH_SHORT);
			    	nieZapisanoToast.show();    
		    	}
		    	else{
		    	    Toast zapisanoToast = Toast.makeText(getApplicationContext(), 
		    	    				"Rysunek zapisano do Galerii!", Toast.LENGTH_SHORT);
		    	    zapisanoToast.show();
		    	}
   	
		    }
		});
		//Dodaj przycisk "Anuluj"
		zapiszDialog.setNegativeButton("Anuluj", new DialogInterface.OnClickListener(){
		    public void onClick(DialogInterface dialog, int which){
		        dialog.cancel();
		    }
		});
		zapiszDialog.show();
	}
	
	@Override
	public void onClick(View view){
	//Wywo�ywany podczas przyciskania przycisk�w
		
		switch(view.getId()) {
		
			case(R.id.button_rozmiar_pedzla): 	pokazOknoRozmiarPedzla();
												break;
				
			case(R.id.button_nowa_kartka) :		pokazOknoNowaKartka();
			    								break;
	
		    case(R.id.button_zapisz):			pokazOknoZapisu();
            
		}//switch
	}
	
	public void paintClicked(View widok){
			
		//Sprawd�, czy u�ytkownik nie klikn�� aktualnie wybranej farby
		if(widok != buttonAktualnyKolor){
			
			ImageButton wybrany_przycisk = (ImageButton)widok;	//Zapisz referencj� do przycisku
			String kolor = widok.getTag().toString();			//Zapisz tag wybranego przycisku
			widokRys.zastosujKolor(kolor);						//Zmie� kolor rysowanych linii
			
			//Zmie� wygl�d nowo wybranego przycisku na palecie kolor�w
			wybrany_przycisk.setImageDrawable(getResources().getDrawable(R.drawable.przycisk_wybrany));
			//Zmie� poprzednio wybrany na stan "niewybrany"
			buttonAktualnyKolor.setImageDrawable(getResources().getDrawable(R.drawable.przycisk_niewybrany));
			//Zapisz referencj� do aktualnie wybranego przycisku w elemencie klasy GlownaAktywnosc
			buttonAktualnyKolor = wybrany_przycisk;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Stw�rz menu na podstawie glowna_aktywnosc.xml
		getMenuInflater().inflate(R.menu.glowna_aktywnosc, menu);
		// Zwr�� true, aby pokaza� utworzone menu
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
}
