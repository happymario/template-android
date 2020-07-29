#include <SoftwareSerial.h>
SoftwareSerial BTSerial(11, 12); //블루투스 설정 BTSerial(Tx, Rx)

int green = 5; // 12 port
int yellow = 3; // 10 port
int red = 2; // 8 port

void setup() {
  BTSerial.begin(9600); // 블루투스 통신 시작
  // put your setup code here, to run once:
  pinMode(green, OUTPUT);
  pinMode(yellow, OUTPUT);
  pinMode(red, OUTPUT);
}

void loop() {
 if(BTSerial.available())        //값이 들어오면
  {
    char bt;                     //제어할 변수 bt선언
    bt = BTSerial.read();        //들어온 값을 bt에 저장
    if(bt == 'a')                //bt가 a면
      digitalWrite(green, HIGH); //초록불 ON
    if(bt == 'b')
      digitalWrite(yellow, HIGH);
    if(bt == 'c')
      digitalWrite(red, HIGH);
    if(bt == 'd')
      digitalWrite(green, LOW);
    if(bt == 'e')
      digitalWrite(yellow, LOW);
    if(bt == 'f')
      digitalWrite(red, LOW);
  }

  int greenValue = digitalRead(green);
  int redValue = digitalRead(red);
  int yellowredValue = digitalRead(yellow);

  BTSerial.print('\r'); // Bluetooth SPP는 이렇게 시작해야 한다. 
  BTSerial.print(redValue);
  BTSerial.print(",");
  BTSerial.print(greenValue);
  BTSerial.print(",");
  BTSerial.print(redValue);
  BTSerial.print(";");
  
  delay(1000);
}
