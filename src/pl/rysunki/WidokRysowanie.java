package pl.rysunki;

import android.view.View;
import android.content.Context;	  //Wykorzyst. w konstruktorze
import android.util.AttributeSet; //Wykorzyst. w konstruktorze

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

//Do zmieniania rozmiaru p�dzla
import android.graphics.PorterDuff;
import android.util.TypedValue;
//--

public class WidokRysowanie extends View {

	//Pola klasy
	private Path 	sciezka; 					//Scie�ka
	private Paint 	farba, farbaKanwy; 			//farba
	private int 	kolorFarby = 0xD067D3; 		//Pocz�tkowy kolor farby (ciemny fiolet)
	private Canvas 	kanwa; 						//Kanwa
	private Bitmap 	bitmapaKanwy; 				//Bitmapa nale��ca do kanwy
	private float rozmiarPedzla, popRozmiarPedzla; //Rozmiary p�dzla
	
	//Konstruktor
	public WidokRysowanie(Context k, AttributeSet attrs){
	    super(k, attrs);
	    przygotujPowierzchnie();
	}
	
	private void przygotujPowierzchnie(){
		
		//Pocz�tkowy rozmiar p�dzla to �redni 
		rozmiarPedzla = getResources().getInteger(R.integer.sredni_rozmiar);
		//Zapisz rozmiar, aby w razie anulowania ostatniej
		//akcji m�c wr�ci� do poprzedniego rozmiaru
		popRozmiarPedzla = rozmiarPedzla;
		
		//Przygotuj powierzchni� do rysowania (interakcji)      
		//Zainicjuj niekt�re pola klasy
		sciezka  = new Path();
		farba = new Paint();
		
		//Przypisz farbie pocz�tkowy kolor
		farba.setColor(kolorFarby);
		
		//Przypisz inne w�a�ciwo�ci farby wp�ywaj�ce na ob�y wygl�d p�dzla
		farba.setAntiAlias(true); 
		farba.setStrokeWidth(rozmiarPedzla); //U�yj rozmiaru p�dzla
		farba.setStyle(Paint.Style.STROKE);
		farba.setStrokeJoin(Paint.Join.ROUND);
		farba.setStrokeCap(Paint.Cap.ROUND);
		
		//Stw�rz kanw�
		farbaKanwy = new Paint(Paint.DITHER_FLAG);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		//Wywo�aj metod� klasy View
		super.onSizeChanged(w, h, oldw, oldh);
		
		//Dostosuj rozmiar bitmapy do nowego ekranu
		bitmapaKanwy = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		kanwa = new Canvas(bitmapaKanwy);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		//Rysowanie bitmapy aktualn� farb�
		canvas.drawBitmap(bitmapaKanwy, 0, 0, farbaKanwy);
		//Rysowanie �cie�ki aktualn� farb�
		canvas.drawPath(sciezka, farba);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent zdarzenie) {
		
		//Zapisz wsp�rz�dne dotyku
		float wspolrzednaX = zdarzenie.getX();
		float wspolrzednaY = zdarzenie.getY(); 
		
		switch (zdarzenie.getAction()) //Rozpoznaj rodzaj ruchu
		{
			case MotionEvent.ACTION_DOWN:
			    sciezka.moveTo(wspolrzednaX, wspolrzednaY);
			    break;
			case MotionEvent.ACTION_MOVE:
			    sciezka.lineTo(wspolrzednaX, wspolrzednaY);
			    break;
			case MotionEvent.ACTION_UP:
			    kanwa.drawPath(sciezka, farba);
			    sciezka.reset();
			    break;
			default:
			    return false;
		}
		
		invalidate(); //Uaktualnij widok, wywo�uje onDraw
		return true;
	}
	
	public void zastosujKolor(String nowyKolor){
		
		invalidate();   //od�wie� widok
		//Stw�rz kolor na podstawie nazwy
		kolorFarby = Color.parseColor(nowyKolor);
		farba.setColor(kolorFarby);
			}

	public void zastosujRozmiarPedzla(float nowyRozmiar){
	
	//Oblicz rozmiar p�dzla w pikselach
	float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
			nowyRozmiar, getResources().getDisplayMetrics());
	
	//Przypisz wynik zmiennej klasy
	rozmiarPedzla=pixelAmount;
		
	//Zmie� rozmiar p�dzla na nowy, aktualizuj obiekt Paint	
	farba.setStrokeWidth(rozmiarPedzla);
	}

	public void zapiszPopRozmiarPedzla(float popRozmiar){
    popRozmiarPedzla=popRozmiar;
}

	public float zwrocPopRozmiarPedzla(){
    return popRozmiarPedzla;
}

	public void nowaKartka(){
	//Metoda wykorzystywana dla przycisku "nowy" w glowna_aktywnosc
	//Tworzy "now� kartk� w bloku rysunkowym"
    kanwa.drawColor(0, PorterDuff.Mode.CLEAR); //wyczy�� kanw�
    invalidate(); //nanie� zmiany
}
}
