-- Implements a simple Nios II system for the DE2-115 series board
-- Inputs: SW7-0 are parallel port inputs to the Nios II system
--         CLOCK_50 is the system clock
--         KEY0 is the active-low system reset
-- Outputs:LED7-0 are parallel port outputs from the Nios II system

library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_unsigned.all;

entity lab6 is
port (
	CLOCK_50 : in std_logic;
	KEY      : in std_logic_vector(0 downto 0);
	SW       : in std_logic_vector(7 downto 0);
	LEDR     : out std_logic_vector(7 downto 0);
	HEX0     : out std_logic_vector(7 downto 0);
	HEX1     : out std_logic_vector(7 downto 0);
	HEX2     : out std_logic_vector(7 downto 0);
	HEX3     : out std_logic_vector(7 downto 0);
	HEX4     : out std_logic_vector(7 downto 0);
	HEX5     : out std_logic_vector(7 downto 0);
	HEX6     : out std_logic_vector(7 downto 0);
	HEX7     : out std_logic_vector(7 downto 0);
        LCD_BLON : out std_logic;
        LCD_DATA : inout std_logic_vector(7 downto 0);
        LCD_EN   : out std_logic;
        LCD_ON   : out std_logic;
        LCD_RS   : out std_logic;
        LCD_RW   : out std_logic;
        DRAM_DQ  : inout std_logic_vector(31 downto 0);
	DRAM_ADDR: out std_logic_vector(12 downto 0);
        DRAM_BA  : out std_logic_vector(1 downto 0);
  	DRAM_CAS_N : out std_logic;
  	DRAM_RAS_N : out std_logic;
  	DRAM_CLK : out std_logic;
	DRAM_CKE : out std_logic;
	DRAM_CS_N: out std_logic;
	DRAM_WE_N: out std_logic;
	DRAM_DQM : out std_logic_vector(3 downto 0)
	);
end lab6;

architecture lab6_rtl of lab6 is
  component nios_system
      port (
	signal clk_clk : in std_logic;
	signal reset_reset : in std_logic;
	signal switches_export : in std_logic_vector (7 downto 0);
	signal leds_export : out std_logic_vector (7 downto 0);
	signal h0_export : out std_logic_vector (7 downto 0);
	signal h1_export : out std_logic_vector (7 downto 0);
	signal h2_export : out std_logic_vector (7 downto 0);
	signal h3_export : out std_logic_vector (7 downto 0);
	signal h4_export : out std_logic_vector (7 downto 0);
	signal h5_export : out std_logic_vector (7 downto 0);
	signal h6_export : out std_logic_vector (7 downto 0);
	signal h7_export : out std_logic_vector (7 downto 0);
        signal lcd_DATA        : inout std_logic_vector(7 downto 0) := (others => 'X'); 
        signal lcd_ON          : out   std_logic;                                      
        signal lcd_BLON        : out   std_logic;                                      
        signal lcd_EN          : out   std_logic;                                      
        signal lcd_RS          : out   std_logic;                                      
        signal lcd_RW          : out   std_logic;
        signal sdram_clk_clk    : out   std_logic;
	signal sdram_wire_addr  : out   std_logic_vector(12 downto 0);
	signal sdram_wire_ba    : out   std_logic_vector(1 downto 0);
	signal sdram_wire_cas_n : out   std_logic;
	signal sdram_wire_cke   : out   std_logic;
	signal sdram_wire_cs_n  : out   std_logic;
	signal sdram_wire_dq    : inout std_logic_vector(31 downto 0) := (others => 'X');
	signal sdram_wire_dqm   : out   std_logic_vector(3 downto 0);
	signal sdram_wire_ras_n : out   std_logic;
	signal sdram_wire_we_n  : out   std_logic                                         -- we_n
	);
	end component;
	
	signal hexa : std_logic_vector(7 downto 0);
	signal hexb : std_logic_vector(7 downto 0);
	signal hexc : std_logic_vector(7 downto 0);
	signal hexd : std_logic_vector(7 downto 0);
	signal hexe : std_logic_vector(7 downto 0);
	signal hexf : std_logic_vector(7 downto 0);
	signal hexg : std_logic_vector(7 downto 0);
	signal hexh : std_logic_vector(7 downto 0);

begin

	HEX0(0) <= hexa(0);
	HEX0(1) <= hexa(1);
	HEX0(2) <= hexa(2);
	HEX0(3) <= hexa(3);
	HEX0(4) <= hexa(4);
	HEX0(5) <= hexa(5);
	HEX0(6) <= hexa(6);
	HEX1(0) <= hexb(0);
	HEX1(1) <= hexb(1);
	HEX1(2) <= hexb(2);
	HEX1(3) <= hexb(3);
	HEX1(4) <= hexb(4);
	HEX1(5) <= hexb(5);
	HEX1(6) <= hexb(6);
	HEX2(0) <= hexc(0);
	HEX2(1) <= hexc(1);
	HEX2(2) <= hexc(2);
	HEX2(3) <= hexc(3);
	HEX2(4) <= hexc(4);
	HEX2(5) <= hexc(5);
	HEX2(6) <= hexc(6);
	HEX3(0) <= hexd(0);
	HEX3(1) <= hexd(1);
	HEX3(2) <= hexd(2);
	HEX3(3) <= hexd(3);
	HEX3(4) <= hexd(4);
	HEX3(5) <= hexd(5);
	HEX3(6) <= hexd(6);
	HEX4(0) <= hexe(0);
	HEX4(1) <= hexe(1);
	HEX4(2) <= hexe(2);
	HEX4(3) <= hexe(3);
	HEX4(4) <= hexe(4);
	HEX4(5) <= hexe(5);
	HEX4(6) <= hexe(6);
	HEX5(0) <= hexf(0);
	HEX5(1) <= hexf(1);
	HEX5(2) <= hexf(2);
	HEX5(3) <= hexf(3);
	HEX5(4) <= hexf(4);
	HEX5(5) <= hexf(5);
	HEX5(6) <= hexf(6);
	HEX6(0) <= hexg(0);
	HEX6(1) <= hexg(1);
	HEX6(2) <= hexg(2);
	HEX6(3) <= hexg(3);
	HEX6(4) <= hexg(4);
	HEX6(5) <= hexg(5);
	HEX6(6) <= hexg(6);
	HEX7(0) <= hexh(0);
	HEX7(1) <= hexh(1);
	HEX7(2) <= hexh(2);
	HEX7(3) <= hexh(3);
	HEX7(4) <= hexh(4);
	HEX7(5) <= hexh(5);
	HEX7(6) <= hexh(6);

NiosII : nios_system
  port map(
	clk_clk                      => CLOCK_50,
	reset_reset                  => NOT KEY(0),
	switches_export              => SW(7 downto 0),
	leds_export                  => LEDR(7 downto 0),
	h0_export                    => hexa,
	h1_export                    => hexb,
	h2_export                    => hexc,
	h3_export                    => hexd,
	h4_export                    => hexe,
	h5_export                    => hexf,
	h6_export                    => hexg,
	h7_export                    => hexh,
	lcd_BLON                     => LCD_BLON,
	lcd_DATA                     => LCD_DATA,
	lcd_EN                       => LCD_EN,
	lcd_ON                       => LCD_ON,
	lcd_RS                       => LCD_RS,
	lcd_RW                       => LCD_RW,
	sdram_clk_clk                => DRAM_CLK,
	sdram_wire_addr              => DRAM_ADDR,
  	sdram_wire_ba		     => DRAM_BA,
  	sdram_wire_cas_n	     => DRAM_CAS_N,
  	sdram_wire_cke		     => DRAM_CKE,
  	sdram_wire_cs_n		     => DRAM_CS_N,
  	sdram_wire_dq		     => DRAM_DQ,
  	sdram_wire_dqm		     => DRAM_DQM,
  	sdram_wire_ras_n	     => DRAM_RAS_N,
  	sdram_wire_we_n		     => DRAM_WE_N
	);
end lab6_rtl;
