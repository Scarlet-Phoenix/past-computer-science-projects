	nios_system u0 (
		.clk_clk          (<connected-to-clk_clk>),          //        clk.clk
		.h0_export        (<connected-to-h0_export>),        //         h0.export
		.h1_export        (<connected-to-h1_export>),        //         h1.export
		.h2_export        (<connected-to-h2_export>),        //         h2.export
		.h3_export        (<connected-to-h3_export>),        //         h3.export
		.h4_export        (<connected-to-h4_export>),        //         h4.export
		.h5_export        (<connected-to-h5_export>),        //         h5.export
		.h6_export        (<connected-to-h6_export>),        //         h6.export
		.h7_export        (<connected-to-h7_export>),        //         h7.export
		.lcd_DATA         (<connected-to-lcd_DATA>),         //        lcd.DATA
		.lcd_ON           (<connected-to-lcd_ON>),           //           .ON
		.lcd_BLON         (<connected-to-lcd_BLON>),         //           .BLON
		.lcd_EN           (<connected-to-lcd_EN>),           //           .EN
		.lcd_RS           (<connected-to-lcd_RS>),           //           .RS
		.lcd_RW           (<connected-to-lcd_RW>),           //           .RW
		.leds_export      (<connected-to-leds_export>),      //       leds.export
		.reset_reset      (<connected-to-reset_reset>),      //      reset.reset
		.sdram_clk_clk    (<connected-to-sdram_clk_clk>),    //  sdram_clk.clk
		.sdram_wire_addr  (<connected-to-sdram_wire_addr>),  // sdram_wire.addr
		.sdram_wire_ba    (<connected-to-sdram_wire_ba>),    //           .ba
		.sdram_wire_cas_n (<connected-to-sdram_wire_cas_n>), //           .cas_n
		.sdram_wire_cke   (<connected-to-sdram_wire_cke>),   //           .cke
		.sdram_wire_cs_n  (<connected-to-sdram_wire_cs_n>),  //           .cs_n
		.sdram_wire_dq    (<connected-to-sdram_wire_dq>),    //           .dq
		.sdram_wire_dqm   (<connected-to-sdram_wire_dqm>),   //           .dqm
		.sdram_wire_ras_n (<connected-to-sdram_wire_ras_n>), //           .ras_n
		.sdram_wire_we_n  (<connected-to-sdram_wire_we_n>),  //           .we_n
		.switches_export  (<connected-to-switches_export>)   //   switches.export
	);

