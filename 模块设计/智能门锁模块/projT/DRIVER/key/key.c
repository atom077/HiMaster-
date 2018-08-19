#include "key.h"
#include "stm32f10x.h"


extern void delay_ms(u16 nms);

void KEY_init(void){          //  pb15 
	
		GPIO_InitTypeDef GPIO_InitStruct;
		
		//initclock
		RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB,ENABLE);
		
		GPIO_InitStruct.GPIO_Pin=GPIO_Pin_15;
		GPIO_InitStruct.GPIO_Mode=GPIO_Mode_IPU;           //上拉输入
		//GPIO_InitStruct.GPIO_Speed=GPIO_Speed_50MHz;
		GPIO_Init(GPIOB,&GPIO_InitStruct);
		GPIO_SetBits(GPIOB,GPIO_Pin_15);//上拉输入
		
}



u8  getkey(){
		if(!GPIO_ReadInputDataBit(GPIOB, GPIO_Pin_15)){    //低电平   
				delay_ms(100);
				if(!GPIO_ReadInputDataBit(GPIOB, GPIO_Pin_15)){
					while(!GPIO_ReadInputDataBit(GPIOB, GPIO_Pin_15));
						return 1;
				}
		}

		return 0;
}
