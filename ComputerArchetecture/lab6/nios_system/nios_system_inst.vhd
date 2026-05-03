	component nios_system is
		port (
			clk_clk          : in    std_logic                     := 'X';             -- clk
			h0_export        : out   std_logic_vector(7 downto 0);                     -- export
			h1_export        : out   std_logic_vector(7 downto 0);                     -- export
			h2_export        : out   std_logic_vector(7 downto 0);                     -- export
			h3_export        : out   std_logic_vector(7 downto 0);                     -- export
			h4_export        : out   std_logic_vector(7 downto 0);                     -- export
			h5_export        : out   std_logic_vector(7 downto 0);                     -- export
			h6_export        : out   std_logic_vector(7 downto 0);                     -- export
			h7_export        : out   std_logic_vector(7 downto 0);                     -- export
			lcd_DATA         : inout std_logic_vector(7 downto 0)  := (others => 'X'); -- DATA
			lcd_ON           : out   std_logic;                                        -- ON
			lcd_BLON         : out   std_logic;                                        -- BLON
			lcd_EN           : out   std_logic;                                        -- EN
			lcd_RS           : out   std_logic;                                        -- RS
			lcd_RW           : out   std_logic;                                        -- RW
			leds_export      : out   std_logic_vector(7 downto 0);                     -- export
			reset_reset      : in    std_logic                     := 'X';             -- reset
			sdram_clk_clk    : out   std_logic;                                        -- clk
			sdram_wire_addr  : out   std_logic_vector(12 downto 0);                    -- addr
			sdram_wire_ba    : out   std_logic_vector(1 downto 0);                     -- ba
			sdram_wire_cas_n : out   std_logic;                                        -- cas_n
			sdram_wire_cke   : out   std_logic;                                        -- cke
			sdram_wire_cs_n  : out   std_logic;                                        -- cs_n
			sdram_wire_dq    : inout std_logic_vector(31 downto 0) := (others => 'X'); -- dq
			sdram_wire_dqm   : out   std_logic_vector(3 downto 0);                     -- dqm
			sdram_wire_ras_n : out   std_logic;                                        -- ras_n
			sdram_wire_we_n  : out   std_logic;                                        -- we_n
			switches_export  : in    std_logic_vector(7 downto 0)  := (others => 'X')  -- export
		);
	end component nios_system;

	u0 : component nios_system
		port map (
			clk_clk          => CONNECTED_TO_clk_clk,          --        clk.clk
			h0_export        => CONNECTED_TO_h0_export,        --         h0.export
			h1_export        => CONNECTED_TO_h1_export,        --         h1.export
			h2_export        => CONNECTED_TO_h2_export,        --         h2.export
			h3_export        => CONNECTED_TO_h3_export,        --         h3.export
			h4_export        => CONNECTED_TO_h4_export,        --         h4.export
			h5_export        => CONNECTED_TO_h5_export,        --         h5.export
			h6_export        => CONNECTED_TO_h6_export,        --         h6.export
			h7_export        => CONNECTED_TO_h7_export,        --         h7.export
			lcd_DATA         => CONNECTED_TO_lcd_DATA,         --        lcd.DATA
			lcd_ON           => CONNECTED_TO_lcd_ON,           --           .ON
			lcd_BLON         => CONNECTED_TO_lcd_BLON,         --           .BLON
			lcd_EN           => CONNECTED_TO_lcd_EN,           --           .EN
			lcd_RS           => CONNECTED_TO_lcd_RS,           --           .RS
			lcd_RW           => CONNECTED_TO_lcd_RW,           --           .RW
			leds_export      => CONNECTED_TO_leds_export,      --       leds.export
			reset_reset      => CONNECTED_TO_reset_reset,      --      reset.reset
			sdram_clk_clk    => CONNECTED_TO_sdram_clk_clk,    --  sdram_clk.clk
			sdram_wire_addr  => CONNECTED_TO_sdram_wire_addr,  -- sdram_wire.addr
			sdram_wire_ba    => CONNECTED_TO_sdram_wire_ba,    --           .ba
			sdram_wire_cas_n => CONNECTED_TO_sdram_wire_cas_n, --           .cas_n
			sdram_wire_cke   => CONNECTED_TO_sdram_wire_cke,   --           .cke
			sdram_wire_cs_n  => CONNECTED_TO_sdram_wire_cs_n,  --           .cs_n
			sdram_wire_dq    => CONNECTED_TO_sdram_wire_dq,    --           .dq
			sdram_wire_dqm   => CONNECTED_TO_sdram_wire_dqm,   --           .dqm
			sdram_wire_ras_n => CONNECTED_TO_sdram_wire_ras_n, --           .ras_n
			sdram_wire_we_n  => CONNECTED_TO_sdram_wire_we_n,  --           .we_n
			switches_export  => CONNECTED_TO_switches_export   --   switches.export
		);

