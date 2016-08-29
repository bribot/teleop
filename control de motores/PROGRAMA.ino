#include <avr/interrupt.h>
#include <avr/io.h>       
#include <avr/sfr_defs.h>
#include <stdio.h>        


int sign=0;
int flag=0;
long ro=0;
long tac=0;
long t2ac=0;
long t1ac=0; 
float cacc=0;
float cve=0;
unsigned long t=0;
unsigned long t1=0;
unsigned long t2=0;
float error=0;
float e=0;
int gvv=0;
int gvvI1=0;
volatile long y1=8096;
volatile long y=8096;
volatile int q=0;
volatile int q1=0;
long r=8096;
double kp=100;
double kd=0.3;
double ki=0.01;
double kpI1=100;
double kdI1=0.3;
double kiI1=0.01;
long fac=0;
float velt=0;
float velx=0;
float vel=0;
float vel1=0;
float acc=0;
long x=0;
long w=0;
long w1=0;

//***********
long roI1=0;
long tacI1=0;
long t2acI1=0;
long t1acI1=0; 
float caccI1=0;
float cveI1=0;
unsigned long tI1=0;
unsigned long t1I1=0;
unsigned long t2I1=0;
float errorI1=0;
float eI1=0;
volatile long yI1=8096;
volatile int qI1=0;
long rI1=8096;
long facI1=0;
float veltI1=0;
float velxI1=0;
float velI1=0;
float vel1I1=0;
float accI1=0;
long xI1=0;
long wI1=0;
long w1I1=0;
//***********

int incomingByte = 0;   // for incoming serial data
float dato[6]={1,2,3,4,5,6} ;
int con1=0;
int f1=0;
int f3=0;
int f4=0;
int i=0;

int rep=0;
int ini=0;
int ini2=0;
int ini3=0;
float vf=0; 
float vf1=0;
float vf2=0;
float vf4=0;
float datofinal=0;
//serial//
int v1=0;
int wt=1;
long unsigned h=8;
long unsigned h1=8;
int re=0;
int di=0;
int rr=0;


void setup() {
  
attachInterrupt(0, blink, RISING);
attachInterrupt(1, blink1, RISING);
pinMode (9, OUTPUT);
pinMode (10, OUTPUT);
pinMode (5, OUTPUT);
pinMode (6, OUTPUT);
pinMode (13, OUTPUT);
DDRD = DDRD | B01100000;
Serial.begin(9600);
gvv=0;
gvvI1=0;
digitalWrite(13,HIGH);
digitalWrite(12,HIGH);
analogWrite(9,0);
analogWrite(10,0);
analogWrite(5,0);
analogWrite(6,0);

  }
void loop() { 
 
  
 if(Serial.available()>0){
  
   if(flag==0){
   
  gvv=(Serial.read()-110);
   
    //Serial.print("A");
 //  Serial.write(gvv);
   Serial.flush();
    flag=1;
  }
     
  }
if(Serial.available()>0){
  if(flag==1){
 gvvI1=(Serial.read()-110);
     //Serial.print("B");
   // Serial.write(gvvI1);
   Serial.flush();
    flag=0;
  }
   

  }
 
 
 
 
 
 
  // cntrl #1
   
  e=gvv-vel;
  fac=round(kp*e+ki*w-kd*acc);
  if(fac>=255){fac=255;}
  if(fac<=-255){fac=-255;}
  if(fac>=0){
  analogWrite(5,0);
  analogWrite(6,fac);
}
  if(fac<0){
   analogWrite(6,0);
   analogWrite(5,(-1*fac)); 
           }

 if(gvv>=vel){error= gvv-vel;}
 if (gvv<vel){error=vel-gvv;}
  
//#1
  if(x==200){
   
   if(y!=r){
    if(y>4000 && y<12192){
    t=millis();
    t2=t-t1;
    t1=t;
    cve=y-r;
    if(cve!=0){velx=cve/t2;
    if(abs(velx-velt)<=10) {vel=velx; velt=velx;}
    if(abs(velx-velt)>10) {vel=velt;} }
    if(cve==0){vel=0;
 }
 
    if(ro==3){
    tac=millis();
    t2ac=tac-t1ac;
    t1ac=tac; 
    cacc=error-vel1;
    if(cve!=0){acc=cacc/t2ac;}
    if(cve==0){acc=0;}
    vel1=error;
 
  w=w1+t2ac*e;
  if((ki*w)>=255){w=w1;}
  w1=w;
  ro=0;
 }
 ro++;
 r=y;
// Serial.println(t2, DEC);
}}
   
  if(y<=4000 || y>=12192){
    t=millis();
    t2=t-t1;
    t1=t;
    y=8096;
   r=y;
   }
   x=0;}
x++;
//-------------------------------------------------------------------------------------------------------------------------
 // cntrl #2
   
  eI1=gvvI1-velI1;
  facI1=round(kpI1*eI1+kiI1*wI1-kdI1*accI1);
  if(facI1>=255){facI1=255;}
  if(facI1<=-255){facI1=-255;}
  if(facI1>=0){
  analogWrite(9,0);
  analogWrite(10,facI1);
}
  if(facI1<0){
   analogWrite(10,0);
   analogWrite(9,(-1*facI1)); 
           }

 if(gvvI1>=velI1){errorI1= gvvI1-velI1;}
 if (gvvI1<velI1){errorI1=velI1-gvvI1;}
  
//#2
  if(xI1==200){
   
   if(yI1!=rI1){
    if(yI1>4000 && yI1<12192){
    tI1=millis();
    t2I1=tI1-t1I1;
    t1I1=tI1;
    cveI1=yI1-rI1;
    if(cveI1!=0){velxI1=cveI1/t2I1;
    if(abs(velxI1-veltI1)<=10) {velI1=velxI1; veltI1=velxI1;}
    if(abs(velxI1-veltI1)>10) {velI1=veltI1;} }
    if(cveI1==0){velI1=0;
 }
 
    if(roI1==3){
    tacI1=millis();
    t2acI1=tacI1-t1acI1;
    t1acI1=tacI1; 
    caccI1=errorI1-vel1I1;
    if(cveI1!=0){accI1=caccI1/t2acI1;}
    if(cveI1==0){accI1=0;}
    vel1I1=errorI1;
 
  wI1=w1I1+t2acI1*eI1;
  if((kiI1*wI1)>=255){wI1=w1I1;}
  w1I1=wI1;
  roI1=0;
 }
 roI1++;
 rI1=yI1;
// Serial.println(vel, DEC);
 }}
   
  if(yI1<=4000 || yI1>=12192){
    tI1=millis();
    t2I1=tI1-t1I1;
    t1I1=tI1;
    yI1=8096;
   rI1=yI1;
   }
   xI1=0;}
xI1++;
 
}



void blink()
{
 q=(PIND & B00010000);
  if(q==0){
 y++;}
 if(q==16){
 y=y-1;}
 
}



void blink1()
{
 qI1=(PIND & B10000000);
  if(qI1==128) {
 yI1++;}
 if(qI1==0){
 yI1=yI1-1;}
}
