#include "beep.h"
#include "stm32f10x.h"
void BEEP_init(void){
	
		GPIO_InitTypeDef GPIO_InitStruct;
		
		//initclock
		RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB,ENABLE);
		
		GPIO_InitStruct.GPIO_Pin=GPIO_Pin_8;
		GPIO_InitStruct.GPIO_Mode=GPIO_Mode_Out_PP;
		GPIO_InitStruct.GPIO_Speed=GPIO_Speed_50MHz;
	
	
		GPIO_Init(GPIOB,&GPIO_InitStruct);
		GPIO_SetBits(GPIOB,GPIO_Pin_8);//pb8输出高 不导通三极管 关蜂鸣器
		
}
