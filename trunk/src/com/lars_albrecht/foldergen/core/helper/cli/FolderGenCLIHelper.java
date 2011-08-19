/*
 * Copyright (c) 2011 Lars Chr. Albrecht
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

/**
 * 
 */
package com.lars_albrecht.foldergen.core.helper.cli;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.lars_albrecht.foldergen.core.helper.properies.PropertiesReader;

/**
 * Contains helper for the command line.
 * 
 * @author lalbrecht
 * @version 1.5.0.0
 */
public final class FolderGenCLIHelper {

	/**
	 * Construct and provide GNU-compatible Options.
	 * 
	 * @return Options expected from command-line of GNU form.
	 */
	public static Options createOptions() {
		final Options fgOptions = new Options();
		fgOptions.addOption("c", "config", true,
				PropertiesReader.getInstance().getProperties("application.cli.parameters.config")).addOption("g", "gui", false,
				PropertiesReader.getInstance().getProperties("application.cli.parameters.gui")).addOption("d", "debug", false,
				PropertiesReader.getInstance().getProperties("application.cli.parameters.debug")).addOption("r", "root", true,
				PropertiesReader.getInstance().getProperties("application.cli.parameters.root")).addOption("co", "confirmation",
				false, PropertiesReader.getInstance().getProperties("application.cli.parameters.confirmation")).addOption("l",
				"locale", true, PropertiesReader.getInstance().getProperties("application.cli.parameters.locale")).addOption("p",
				"plugins", true, PropertiesReader.getInstance().getProperties("application.cli.parameters.plugins")).addOption(
				"px", "proxy", false, PropertiesReader.getInstance().getProperties("application.cli.parameters.proxy"))
				.addOption("o", "overwrite", true,
						PropertiesReader.getInstance().getProperties("application.cli.parameters.overwrite"));
		;
		fgOptions.addOptionGroup(new OptionGroup().addOption(new Option("h", "help", false, PropertiesReader.getInstance()
				.getProperties("application.cli.parameters.help"))));
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
	 * @throws ParseException
	 */
	public static FolderGenCLIConf parseArguments(final String[] commandLineArguments) throws ParseException {
		final FolderGenCLIConf conf = new FolderGenCLIConf();
		final CommandLineParser cmdLineGnuParser = new GnuParser();
		final Options options = FolderGenCLIHelper.createOptions();
		CommandLine commandLine = null;
		commandLine = cmdLineGnuParser.parse(options, commandLineArguments);
		if(commandLine.hasOption("config") || commandLine.hasOption("c")) {
			conf.setConfigFile(new File(commandLine.hasOption("config") ? commandLine.getOptionValue("config") : commandLine
					.getOptionValue("c")));
		}
		if(commandLine.hasOption("root") || commandLine.hasOption("r")) {
			conf.setRootPath(new File(commandLine.hasOption("root") ? commandLine.getOptionValue("root") : commandLine
					.getOptionValue("r")));
		}
		if(commandLine.hasOption("g") || commandLine.hasOption("gui")) {
			conf.setIsGui(Boolean.TRUE);
		}
		if(commandLine.hasOption("d") || commandLine.hasOption("debug")) {
			conf.setIsDebug(Boolean.TRUE);
		}
		if(commandLine.hasOption("co") || commandLine.hasOption("confirmation")) {
			conf.setShowConfirmation(Boolean.TRUE);
		}
		if(commandLine.hasOption("locale") || commandLine.hasOption("l")) {
			conf.setLocale(new Locale(commandLine.hasOption("locale") ? commandLine.getOptionValue("locale") : commandLine
					.getOptionValue("l")));
		}
		if(commandLine.hasOption("p") || commandLine.hasOption("plugins")) {
			conf.setUsePlugins(Boolean.TRUE);
		}
		if(commandLine.hasOption("px") || commandLine.hasOption("proxy")) {
			conf.setUseProxy(Boolean.TRUE);
		}
		if(commandLine.hasOption("h") || commandLine.hasOption("help")) {
			FolderGenCLIHelper.printHelp(FolderGenCLIHelper.createOptions(), 80, PropertiesReader.getInstance().getProperties(
					"application.cli.seperator"), PropertiesReader.getInstance().getProperties("application.cli.seperator"), 1,
					2, true, System.out);
			System.exit(-1);
		}
		if(commandLine.hasOption("overwrite") || commandLine.hasOption("o")) {
			String overwriteParam = commandLine.hasOption("overwrite") ? commandLine.getOptionValue("overwrite") : commandLine
					.getOptionValue("o");
			if(overwriteParam.equals("no")) {
				conf.setOverwrite(FolderGenCLIConf.OVERWRITE_OFF);
			} else if(overwriteParam.equals("force")) {
				conf.setOverwrite(FolderGenCLIConf.OVERWRITE_FORCE);
			} else if(overwriteParam.equals("ask")) {
				conf.setOverwrite(FolderGenCLIConf.OVERWRITE_ASK);
			}

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
		final String commandLineSyntax = PropertiesReader.getInstance().getProperties("application.output.commandlinesyntax");
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
	 * @throws IOException
	 */
	public static void displayProvidedCommandLineArguments(final String[] commandLineArguments, final OutputStream out)
			throws IOException {
		final StringBuffer buffer = new StringBuffer();
		for(final String argument : commandLineArguments) {
			buffer.append(argument).append(" ");
		}
		out.write((buffer.toString() + "\n").getBytes());
	}

}
