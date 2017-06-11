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

//Do zmieniania rozmiaru pędzla
import android.graphics.PorterDuff;
import android.util.TypedValue;
//--

public class WidokRysowanie extends View {

	//Pola klasy
	private Path 	sciezka; 			//Scieżka
	private Paint 	farba, farbaKanwy; 		//farba
	private int 	kolorFarby = 0xD067D3; 		//Początkowy kolor farby (ciemny fiolet)
	private Canvas 	kanwa; 				//Kanwa
	private Bitmap 	bitmapaKanwy; 			//Bitmapa należąca do kanwy
	private float rozmiarPedzla, popRozmiarPedzla; //Rozmiary pędzla
	
	//Konstruktor
	public WidokRysowanie(Context k, AttributeSet attrs){
	    super(k, attrs);
	    przygotujPowierzchnie();
	}
	
	private void przygotujPowierzchnie(){
		
		//Początkowy rozmiar pędzla to średni 
		rozmiarPedzla = getResources().getInteger(R.integer.sredni_rozmiar);
		//Zapisz rozmiar, aby w razie anulowania ostatniej
		//akcji móc wrócić do poprzedniego rozmiaru
		popRozmiarPedzla = rozmiarPedzla;
		
		//Przygotuj powierzchnię do rysowania (interakcji)      
		//Zainicjuj niektóre pola klasy
		sciezka  = new Path();
		farba = new Paint();
		
		//Przypisz farbie początkowy kolor
		farba.setColor(kolorFarby);
		
		//Przypisz inne właściwości farby wpływające na obły wygląd pędzla
		farba.setAntiAlias(true); 
		farba.setStrokeWidth(rozmiarPedzla); //Użyj rozmiaru pędzla
		farba.setStyle(Paint.Style.STROKE);
		farba.setStrokeJoin(Paint.Join.ROUND);
		farba.setStrokeCap(Paint.Cap.ROUND);
		
		//Stwórz kanwę
		farbaKanwy = new Paint(Paint.DITHER_FLAG);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		//Wywołaj metodę klasy View
		super.onSizeChanged(w, h, oldw, oldh);
		
		//Dostosuj rozmiar bitmapy do nowego ekranu
		bitmapaKanwy = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		kanwa = new Canvas(bitmapaKanwy);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		//Rysowanie bitmapy aktualną farbą
		canvas.drawBitmap(bitmapaKanwy, 0, 0, farbaKanwy);
		//Rysowanie ścieżki aktualną farbą
		canvas.drawPath(sciezka, farba);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent zdarzenie) {
		
		//Zapisz współrzędne dotyku
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
		
		invalidate(); //Uaktualnij widok, wywołuje onDraw
		return true;
	}
	
	public void zastosujKolor(String nowyKolor){
		
		invalidate();   //odśwież widok
		//Stwórz kolor na podstawie nazwy
		kolorFarby = Color.parseColor(nowyKolor);
		farba.setColor(kolorFarby);
			}

	public void zastosujRozmiarPedzla(float nowyRozmiar){
	
	//Oblicz rozmiar pędzla w pikselach
	float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
			nowyRozmiar, getResources().getDisplayMetrics());
	
	//Przypisz wynik zmiennej klasy
	rozmiarPedzla=pixelAmount;
		
	//Zmień rozmiar pędzla na nowy, aktualizuj obiekt Paint	
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
	//Tworzy "nową kartkę w bloku rysunkowym"
    kanwa.drawColor(0, PorterDuff.Mode.CLEAR); //wyczyść kanwę
    invalidate(); //nanieś zmiany
}
}
