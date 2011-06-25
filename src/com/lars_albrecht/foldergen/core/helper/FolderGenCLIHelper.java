/**
 * 
 */
package com.lars_albrecht.foldergen.core.helper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Contains helper for the command line.
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
public final class FolderGenCLIHelper {

	/**
	 * Construct and provide GNU-compatible Options.
	 * 
	 * @return Options expected from command-line of GNU form.
	 */
	public static Options createOptions() {
		final Options fgOptions = new Options();
		fgOptions.addOption("c", "config", true, "Config file (*.foldergenconf)").addOption("g", "gui", false, "use GUI")
				.addOption("d", "debug", false, "Show debug prints");
		fgOptions.addOptionGroup(new OptionGroup().addOption(new Option("h", "help", false, "Show this help entry")));
		return fgOptions;
	}

	/**
	 * Write the provided number of blank lines to the provided OutputStream.
	 * 
	 * @param numberBlankLines
	 *            Number of blank lines to write.
	 * @param out
	 *            OutputStream to which to write the blank lines.
	 */
	public static void displayBlankLines(final int numberBlankLines, final OutputStream out) {
		try {
			for(int i = 0; i < numberBlankLines; ++i) {
				out.write("\n".getBytes());
			}
		} catch(IOException ioEx) {
			for(int i = 0; i < numberBlankLines; ++i) {
				System.out.println();
			}
		}
	}

	/**
	 * Apply Apache Commons CLI GnuParser to command-line arguments.
	 * 
	 * @param commandLineArguments
	 *            Command-line arguments to be processed with Gnu-style parser.
	 * @return FolderGenCLIConf conf
	 */
	public static FolderGenCLIConf parseArguments(final String[] commandLineArguments) {
		final FolderGenCLIConf conf = new FolderGenCLIConf();
		final CommandLineParser cmdLineGnuParser = new GnuParser();
		final Options gnuOptions = FolderGenCLIHelper.createOptions();
		CommandLine commandLine = null;
		try {
			commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);
			if(commandLine.hasOption("config")) {
				conf.setFile(new File(commandLine.getOptionValue("config")));
			}
			if(commandLine.hasOption("c")) {
				conf.setFile(new File(commandLine.getOptionValue("c")));
			}
			if(commandLine.hasOption("g") || commandLine.hasOption("gui")) {
				conf.setIsGui(Boolean.TRUE);
			}
			if(commandLine.hasOption("d") || commandLine.hasOption("debug")) {
				conf.setIsDebug(Boolean.TRUE);
			}
			if(commandLine.hasOption("h") || commandLine.hasOption("help")) {
				FolderGenCLIHelper.printHelp(FolderGenCLIHelper.createOptions(), 80, "---------------------------",
						"---------------------------", 1, 2, true, System.out);
				System.exit(-1);
			}
		} catch(ParseException parseException) {
			System.err.println("Encountered exception while parsing using Apache Commons CLI Parser:\n"
					+ parseException.getMessage());
		}
		return conf;
	}

	/**
	 * Print usage information to provided OutputStream.
	 * 
	 * @param applicationName
	 *            Name of application to list in usage.
	 * @param options
	 *            Command-line options to be part of usage.
	 * @param out
	 *            OutputStream to which to write the usage information.
	 */
	public static void printUsage(final String applicationName, final Options options, final OutputStream out) {
		final PrintWriter writer = new PrintWriter(out);
		final HelpFormatter usageFormatter = new HelpFormatter();
		usageFormatter.printUsage(writer, 80, applicationName, options);
		writer.close();
	}

	/**
	 * Write "help" to the provided OutputStream.
	 */
	public static void printHelp(final Options options, final int printedRowWidth, final String header, final String footer,
			final int spacesBeforeOption, final int spacesBeforeOptionDescription, final boolean displayUsage,
			final OutputStream out) {
		final String commandLineSyntax = "java -cp ApacheCommonsCLI.jar";
		final PrintWriter writer = new PrintWriter(out);
		final HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(writer, printedRowWidth, commandLineSyntax, header, options, spacesBeforeOption,
				spacesBeforeOptionDescription, footer, displayUsage);
		writer.close();
	}

	/**
	 * Display command-line arguments without processing them in any further way.
	 * 
	 * @param commandLineArguments
	 *            Command-line arguments to be displayed.
	 */
	public static void displayProvidedCommandLineArguments(final String[] commandLineArguments, final OutputStream out) {
		final StringBuffer buffer = new StringBuffer();
		for(final String argument : commandLineArguments) {
			buffer.append(argument).append(" ");
		}
		try {
			out.write((buffer.toString() + "\n").getBytes());
		} catch(IOException ioEx) {
			System.err.println("WARNING: Exception encountered trying to write to OutputStream:\n" + ioEx.getMessage());
			System.out.println(buffer.toString());
		}
	}

}
