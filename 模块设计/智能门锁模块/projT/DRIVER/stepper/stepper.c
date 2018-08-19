#include "stepper.h"
#include "delay.h"
#include "stm32f10x.h"
void STEPPER_init(void){  // PA 0 1 2 3 
		
		GPIO_InitTypeDef GPIO_InitStructure;
		
		//initclock
		RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOC,ENABLE);
		
		GPIO_InitStructure.GPIO_Pin=GPIO_Pin_0;
		GPIO_InitStructure.GPIO_Mode=GPIO_Mode_Out_PP;
		GPIO_InitStructure.GPIO_Speed=GPIO_Speed_50MHz;
		GPIO_Init(GPIOA,&GPIO_InitStructure);
		GPIO_ResetBits(GPIOA,GPIO_Pin_0);
		GPIO_InitStructure.GPIO_Pin=GPIO_Pin_1;
		GPIO_Init(GPIOA,&GPIO_InitStructure);
	
		GPIO_InitStructure.GPIO_Pin=GPIO_Pin_2;
		GPIO_Init(GPIOA,&GPIO_InitStructure);
	
		GPIO_InitStructure.GPIO_Pin=GPIO_Pin_3;
		GPIO_Init(GPIOA,&GPIO_InitStructure);
		
		GPIO_ResetBits(GPIOA,GPIO_Pin_0);
		GPIO_ResetBits(GPIOA,GPIO_Pin_1);
		GPIO_ResetBits(GPIOA,GPIO_Pin_2);
		GPIO_ResetBits(GPIOA,GPIO_Pin_3);
		
}
void stepper_right(){   // 8   4  2  1   正传

	GPIO_SetBits(GPIOA,GPIO_Pin_0);  
	GPIO_ResetBits(GPIOA,GPIO_Pin_1);
	GPIO_ResetBits(GPIOA,GPIO_Pin_2);
	GPIO_ResetBits(GPIOA,GPIO_Pin_3);
	delay_ms(5);
	GPIO_ResetBits(GPIOA,GPIO_Pin_0);  
	GPIO_SetBits(GPIOA,GPIO_Pin_1);
	GPIO_ResetBits(GPIOA,GPIO_Pin_2);
	GPIO_ResetBits(GPIOA,GPIO_Pin_3);
	delay_ms(5);
	GPIO_ResetBits(GPIOA,GPIO_Pin_0);  
	GPIO_ResetBits(GPIOA,GPIO_Pin_1);
	GPIO_SetBits(GPIOA,GPIO_Pin_2);
	GPIO_ResetBits(GPIOA,GPIO_Pin_3);
	delay_ms(5);
	GPIO_ResetBits(GPIOA,GPIO_Pin_0);  
	GPIO_ResetBits(GPIOA,GPIO_Pin_1);
	GPIO_ResetBits(GPIOA,GPIO_Pin_2);
	GPIO_SetBits(GPIOA,GPIO_Pin_3);
	delay_ms(5);

}
void stepper_left(){  // 1  2  4 8  反传
	GPIO_ResetBits(GPIOA,GPIO_Pin_0);  
	GPIO_ResetBits(GPIOA,GPIO_Pin_1);
	GPIO_ResetBits(GPIOA,GPIO_Pin_2);
	GPIO_SetBits(GPIOA,GPIO_Pin_3);
	delay_ms(5);
	GPIO_ResetBits(GPIOA,GPIO_Pin_0);  
	GPIO_ResetBits(GPIOA,GPIO_Pin_1);
	GPIO_SetBits(GPIOA,GPIO_Pin_2);
	GPIO_ResetBits(GPIOA,GPIO_Pin_3);
	delay_ms(5);
	GPIO_ResetBits(GPIOA,GPIO_Pin_0);  
	GPIO_SetBits(GPIOA,GPIO_Pin_1);
	GPIO_ResetBits(GPIOA,GPIO_Pin_2);
	GPIO_ResetBits(GPIOA,GPIO_Pin_3);
	delay_ms(5);
	GPIO_SetBits(GPIOA,GPIO_Pin_0);  
	GPIO_ResetBits(GPIOA,GPIO_Pin_1);
	GPIO_ResetBits(GPIOA,GPIO_Pin_2);
	GPIO_ResetBits(GPIOA,GPIO_Pin_3);
	delay_ms(5);
}


void steprightoneround(void){
				int i;
				for(i=0;i<540;i++){
					stepper_right();
				}
}


void stepleftoneround(void){
				int i;
				for(i=0;i<540;i++){
					stepper_left();
				}
}
