#include "beep.h"
#include "stm32f10x.h"
void SWITCH_init(void){             // P B 9 
	
		GPIO_InitTypeDef GPIO_InitStruct;
		
		//initclock
		RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB,ENABLE);
		
		GPIO_InitStruct.GPIO_Pin=GPIO_Pin_9;
		GPIO_InitStruct.GPIO_Mode=GPIO_Mode_Out_PP;
		GPIO_InitStruct.GPIO_Speed=GPIO_Speed_50MHz;
	
	
		GPIO_Init(GPIOB,&GPIO_InitStruct);
		GPIO_ResetBits(GPIOB,GPIO_Pin_9);//pb9输出低 不导通三极管 关蜂鸣器
		
}
