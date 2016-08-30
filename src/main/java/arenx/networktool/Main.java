package arenx.networktool;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		Options options = new Options();

		Option server = Option.builder("s").longOpt("server").optionalArg(true).desc("server mode").build();
		options.addOption(server);

		Option client = Option.builder("c").longOpt("client").optionalArg(true).hasArg().argName("IP").desc("client mode").build();
		options.addOption(client);

		Option port = Option.builder("p").longOpt("port").optionalArg(true).hasArg().argName("port").desc("port to use").build();
		options.addOption(port);

		Option address = Option.builder("a").longOpt("address").optionalArg(true).hasArg().argName("address").desc("address to use").build();
		options.addOption(address);

		Option s_buf_size = Option.builder("sb").longOpt("s_buf_size").optionalArg(true).hasArg().argName("s_buf_size").desc("sending buffsize").build();
		options.addOption(s_buf_size);

		Option time = Option.builder("t").longOpt("time").optionalArg(true).hasArg().argName("time").desc("time in seconds").build();
		options.addOption(time);

		Option thread = Option.builder("tr").longOpt("thread").optionalArg(true).hasArg().argName("thread").desc("number of parallel thread").build();
		options.addOption(thread);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse( options, args);
		} catch (ParseException e) {
			logger.error("Failed to parse command. Caused by: {}", e.getMessage());
			logger.debug("Failed to parse command", e);
			return;
		}

		if (cmd.getOptions().length==0) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(" ", options );
			return;
		}

		if(cmd.hasOption(server.getOpt())) {

			Server.Builder b = Server.builder();

			String add = cmd.getOptionValue(address.getOpt());
			if (add!=null){
				b.setBindAddress(add);
			}

			String p = cmd.getOptionValue(port.getOpt());
			if (p!=null){
				b.setPort(Integer.parseInt(p));
			}

			b.build().start();


		} else if(cmd.hasOption(client.getOpt())){

			Client.Builder b = Client.builder();

			String add = cmd.getOptionValue(address.getOpt());
			if (add!=null){
				b.setServerAddress(add);
			} else {
				logger.error("server address is not assigned");
				return;
			}

			String p = cmd.getOptionValue(port.getOpt());
			if (p!=null){
				b.setPort(Integer.parseInt(p));
			}

			String sb = cmd.getOptionValue(s_buf_size.getOpt());
			if (sb!=null){
				b.setSendingBufferSize(Integer.parseInt(sb));
			}

			String t = cmd.getOptionValue(time.getOpt());
			if (t!=null){
				b.setTime(Long.parseLong(t));
			}

			String tr = cmd.getOptionValue(thread.getOpt());
			if (tr!=null){
				b.setThread(Integer.parseInt(tr));
			}

			b.build().start();

		} else {
			logger.error("mode is not specified");
		}
	}

}
