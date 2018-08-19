#ifndef __STEPPER_H
#define __STEPPER_H

///* the macro dfinition to trigger the led on or off 
// * 1 - off
// - 0 - on
// */
//#define left  0
//#define right 1

//#define stepper(a)	if (a)	\   
//					GPIO_SetBits(GPIOC,GPIO_Pin_13);\
//					GPIO_ResetBits(GPIOC,GPIO_Pin_13);\
//					GPIO_ResetBits(GPIOC,GPIO_Pin_13);\
//					GPIO_ResetBits(GPIOC,GPIO_Pin_13);\
//					delay_ms(5);\
//					else		\
//					GPIO_ResetBits(GPIOC,GPIO_Pin_13)


//初始化led函数
void STEPPER_init(void);
#endif
