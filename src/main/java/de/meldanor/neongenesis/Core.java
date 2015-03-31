/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Kilian Gärtner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.meldanor.neongenesis;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The entry class of the program
 */
public class Core {

    public static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        // Parse the command line
        NeonGenesisOptions options = new NeonGenesisOptions();
        JCommander commander;
        try {
            commander = new JCommander(options, args);
        }catch (ParameterException ex) {
            // Error with the parameters -> display help
            logger.error(ex.getMessage());
            StringBuilder tmp = new StringBuilder();
            new JCommander(options, "-h").usage(tmp);
            logger.info(tmp.toString());
            return;
        }

        // Invoke the actually program
        new NeonGenesis(options, commander);
    }
}
