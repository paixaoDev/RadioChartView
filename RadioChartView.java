package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.devarthur.spfcapp.R;

import java.util.ArrayList;
import java.util.List;

import data.GraficValues;

//Vetor 2D
class Vector2 {
    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 plus (Vector2 vector2){
        return new Vector2(this.x + vector2.x, this.y + vector2.y);
    }

    @Override
    public String toString() {
        return "Vector2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

//Valores para criar linha
class Line {
    Vector2 p1, p2;

    public Line(Vector2 p1, Vector2 p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
}


public class RadioChartView extends View {

    //region Valores necessarios para desenho
    Paint textPaint = new Paint();
    Paint escudoPaint = new Paint();
    Paint numberPaint = new Paint();
    Paint linePaint = new Paint();
    Paint shapePaint = new Paint();
    Paint backLinesPaint = new Paint();
    //endregion

    //Valor pra cortar o grafico
    Vector2 center;
    int quant;                      //Quantidade de pontos que iram existir
    float pi = 3.14f;               //Valor do pi (usado para calculo)
    float raio = 200f;              //Valor do raio externo do grafico
    float centerPading = 120;      //Padding interno que pode ter no grafico

    //Para construir o grafico
    List<GraficValues> graficValues = new ArrayList<>();
    List<Float> values = new ArrayList<>();
    
    List<Vector2> externos = new ArrayList<>(); //parte externa do grafico
    List<Vector2> internos = new ArrayList<>(); //Parte interna
    List<Vector2> backgroundPoints = new ArrayList<>();
    List<Vector2> pontos = new ArrayList<>();

    public RadioChartView(Context context) {
        super(context);
    }

    public RadioChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    //Inicia os valores de paint
    void initPaints (){
        //Texto da categoria
        textPaint.setColor(getContext().getResources().getColor(R.color.colorWhite));
        textPaint.setTextSize(getResources().getDimension(R.dimen.text_12));
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setStrokeWidth(1f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        //Numero
        numberPaint.setColor(getContext().getResources().getColor(R.color.colorWhite));
        numberPaint.setTextSize(getResources().getDimension(R.dimen.text_10));
        numberPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        numberPaint.setStrokeWidth(2f);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        
        //Escudo
        escudoPaint.setColor(getContext().getResources().getColor(R.color.colorRedSPFC6));

        //Linha final
        linePaint.setColor(getContext().getResources().getColor(R.color.colorRedSPFC6));
        linePaint.setPathEffect(new CornerPathEffect(20));
        linePaint.setStrokeWidth(8f);
        linePaint.setStyle(Paint.Style.STROKE);

        //Linha do background
        backLinesPaint.setColor(getContext().getResources().getColor(R.color.colorWhiteTransp35));
        backLinesPaint.setPathEffect(new CornerPathEffect(20));
        backLinesPaint.setStrokeWidth(3f);
        backLinesPaint.setStyle(Paint.Style.STROKE);

        //Shape interno
        shapePaint.setColor(getContext().getResources().getColor(R.color.colorBlackTransp));
        shapePaint.setPathEffect(new CornerPathEffect(20));
        shapePaint.setStyle(Paint.Style.FILL);
    }

    
    public void init(){
        //valor maximo que o grafico pode ter
        externos = createPoints(quant, raio);
        //valor minimo que o grafico poder ter
        internos = createPoints(quant, centerPading);
        //Linhas do background
        backgroundPoints = createBackgroundPoints(6, 30);
        //pontos do valor
        pontos = createPoints(quant, values);
    }

    public void reInit(){
        if(externos == null || externos.size() == 0){
            //valor maximo que o grafico pode ter
            externos = createPoints(quant, raio);
        }

        if(internos == null || internos.size() == 0){
            //valor minimo que o grafico poder ter
            internos = createPoints(quant, centerPading);
        }

        //Linhas do background
        backgroundPoints = createBackgroundPoints(6, 30);

        if(pontos != null || pontos.size() > 0){
            //valor minimo que o grafico poder ter
            pontos.removeAll(pontos);
            //pontos do valor
            pontos = createPoints(quant, values);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int size = MeasureSpec.getSize(widthMeasureSpec);
        int pading = 70;
        raio = size/2 - pading;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        center = new Vector2(getWidth()/2, getHeight()/2-20);

        if(values.size() >= 3) {
            //Desenha o background
            drawBackgroundPath(canvas, backgroundPoints, center, backLinesPaint);
            //Desenha a linha dos pontos
            drawRect(canvas, pontos, center, linePaint);
            //Desenha o shape dos pontos
            drawRect(canvas, pontos, center, shapePaint);
            //Textos dos valores
            drawValues(canvas, 30, 80, 50);
        }
    }

    //region Criação dos valores
    //Cria os pontos para o background
    List<Vector2> createBackgroundPoints (int n, float diference){
        List<Vector2> vecto = new ArrayList<>();
        for(int i = 0; i < n; i ++){
            vecto.addAll(createPoints(quant, (raio + 50) - (diference * i)));
        }
        return vecto;
    }
    //Corta um circulo em um numero de partes passado
    List<Vector2> createPoints (int n, float r){
        List<Vector2> vector2s = new ArrayList<>();
        for(int i = 0; i < n; i ++){
            float degress = i * (360 / n);
            float radian = ((degress *(pi/180)) ); //+ 61.3f);
            float xx = (float)(r * Math.cos(radian));
            float yy = (float)(r * Math.sin(radian));
            vector2s.add(new Vector2(xx, yy));
        }
        return vector2s;
    }
    //Corta um circulo em um numero de partes passado
    List<Vector2> createPoints (int n, List<Float> r){
        List<Vector2> vector2s = new ArrayList<>();
        for(int i = 0; i < n; i ++){
            float degress = i * (360 / n);
            float radian = ((degress *(pi/180)) ); //+ 61.3f);
            //Cara raio tem um valor diferente
            float xx = (float)(r.get(i) * Math.cos(radian));
            float yy = (float)(r.get(i) * Math.sin(radian));
            vector2s.add(new Vector2(xx, yy));
        }
        return  vector2s;
    }
    //Cria valores para adicionarmos a lista
    void createValues (float value, float maxValue){
        float porc = value / maxValue;
        values.add (centerPading + ((raio - centerPading) * (porc)));
    }
    //Cria valores com nome junto
    public void createValues (String name, float value, float max ){
        graficValues.add(new GraficValues(name, value, max));
        createValues(value, max);
    }
    //Muda os valores já existentes
    public void changeValues (List<GraficValues> grafic){
        graficValues = grafic;
        quant = grafic.size();

        this.values.removeAll(this.values);

        for(GraficValues g: grafic){
            createValues(g.getV(), g.getMax());
        }

        reInit();

        //Inicia o layout
        invalidate();
        requestLayout();
    }

    //Cria uma lista de linhas
    List<Line> createLines (List<Vector2> points){
        //Adiciono o primeiro ponto também como ultimo
        points.add(points.get(0));
        List<Line> lines = new ArrayList<>();

        for(int i = 0; i < points.size()-1; i++){
            lines.add(createLine(points.get(i), points.get(i+1)));
        }

        return lines;
    }
    //Cria uma nova linha
    Line createLine (Vector2 start, Vector2 last){
        return new Line(start, last);
    }
    //endregion

    //region Criação dos Desenhos
    //Desenha o react
    void drawRect (Canvas canvas, List<Vector2> points, Vector2 center, Paint p){
        Path path = new Path();
        path.moveTo(points.get(0).x + center.x, points.get(0).y + center.y);
        for(int i = 1;i < points.size(); i ++){
            path.lineTo(points.get(i).x + center.x, points.get(i).y + center.y);
        }
        path.close();
        canvas.drawPath(path, p);
    }
    //Desenha apenas a background
    void drawBackgroundPath (Canvas canvas, List<Vector2> points, Vector2 center, Paint p){
        for(int i = 0; i < (points.size() / quant); i++){
            List<Vector2> vecto = points.subList(quant * i, (quant * i) + quant);
            drawRect(canvas,vecto ,center, p);
        }
    }
    //Desenha a linha do grafico
    void drawLines (Canvas canvas, List<Line> lines, Paint p){
        for(Line l : lines){
            canvas.drawLine(center.x + l.p1.x, center.y + l.p1.y, center.x + l.p2.x, center.y + l.p2.y, p);
        }
    }
    //Desenha o escudo
    void drawEscudo (Canvas canvas, Vector2 position, Vector2 margin, float width, float height, Paint p){

        Path path = new Path();
        float xx = position.x + center.x;
        float yy = position.y + center.y;

        //Adiciono uma margem
        if(margin != null){
            xx += margin.x;
            yy += margin.y;
        }
        //Desenho
        path.moveTo(xx-(width/2),yy- (height/2));//esquerda
        path.lineTo(xx+(width/2),yy- (height/2));//direita
        path.lineTo(xx+(width/2),yy);//direita meio
        path.lineTo(xx,yy+ (height));//meio baixo
        path.lineTo(xx-(width/2),yy);//esquerda meio
        path.close();

        canvas.drawPath(path, p);
    }
    //Desenha os valores
    void drawValues (Canvas canvas, float diference, float width, float height ){
        for(int i = 0 ;i < externos.size(); i++){
            //Desenhar o testo da categoria
            Vector2 p = externos.get(i);
            canvas.drawText(graficValues.get(i).getTit(), center.x + p.x , center.y + p.y, textPaint);
            //Desenhar escudo
            drawEscudo(canvas, p,new Vector2(0, diference), width, height, escudoPaint);
            //Desenhar valores
            int value = (int)graficValues.get(i).getV();
            canvas.drawText(value + (graficValues.get(i).getP() == 1? "%" : ""), center.x + p.x , (center.y + p.y) + (diference + 5), numberPaint);
        }
    }
    //endregion
}
