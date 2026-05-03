
module nios_system (
	clk_clk,
	h0_export,
	h1_export,
	h2_export,
	h3_export,
	h4_export,
	h5_export,
	h6_export,
	h7_export,
	lcd_DATA,
	lcd_ON,
	lcd_BLON,
	lcd_EN,
	lcd_RS,
	lcd_RW,
	leds_export,
	reset_reset,
	sdram_clk_clk,
	sdram_wire_addr,
	sdram_wire_ba,
	sdram_wire_cas_n,
	sdram_wire_cke,
	sdram_wire_cs_n,
	sdram_wire_dq,
	sdram_wire_dqm,
	sdram_wire_ras_n,
	sdram_wire_we_n,
	switches_export);	

	input		clk_clk;
	output	[7:0]	h0_export;
	output	[7:0]	h1_export;
	output	[7:0]	h2_export;
	output	[7:0]	h3_export;
	output	[7:0]	h4_export;
	output	[7:0]	h5_export;
	output	[7:0]	h6_export;
	output	[7:0]	h7_export;
	inout	[7:0]	lcd_DATA;
	output		lcd_ON;
	output		lcd_BLON;
	output		lcd_EN;
	output		lcd_RS;
	output		lcd_RW;
	output	[7:0]	leds_export;
	input		reset_reset;
	output		sdram_clk_clk;
	output	[12:0]	sdram_wire_addr;
	output	[1:0]	sdram_wire_ba;
	output		sdram_wire_cas_n;
	output		sdram_wire_cke;
	output		sdram_wire_cs_n;
	inout	[31:0]	sdram_wire_dq;
	output	[3:0]	sdram_wire_dqm;
	output		sdram_wire_ras_n;
	output		sdram_wire_we_n;
	input	[7:0]	switches_export;
endmodule
