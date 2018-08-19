#include "led.h"
#include "stm32f10x.h"
void LED_init(void){   
		
		GPIO_InitTypeDef GPIO_InitStruct;
		
		//initclock
		RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB,ENABLE);
		
		GPIO_InitStruct.GPIO_Pin=GPIO_Pin_7;
		GPIO_InitStruct.GPIO_Mode=GPIO_Mode_Out_PP;
		GPIO_InitStruct.GPIO_Speed=GPIO_Speed_50MHz;
	
	
		GPIO_Init(GPIOB,&GPIO_InitStruct);
		GPIO_SetBits(GPIOB,GPIO_Pin_7);
		
}

void openled(void){
		GPIO_ResetBits(GPIOB,GPIO_Pin_7);//低电平 点亮
}

void closeled(void){
		GPIO_SetBits(GPIOB,GPIO_Pin_7); //高电平
		
}
