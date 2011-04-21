package monitor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class CpuInfo {
	private double percent;

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public static double getCPULoad(){
		double value=0;
		try {
			String line;
			OutputStream stdin = null;
			InputStream stderr = null;
			InputStream stdout = null;

			// launch EXE and grab stdin/stdout and stderr
			Process process = Runtime.getRuntime ().exec ("C:\\Windows\\System32\\cmd.exe");
			stdin = process.getOutputStream ();
			stderr = process.getErrorStream ();
			stdout = process.getInputStream ();

			// "write" the parms into stdin
			line = "typeperf -sc 1 \"\\processor(_Total)\\% Processor Time\"" + "\n";   
			stdin.write(line.getBytes() );
			stdin.flush();
			stdin.close();

			// clean up if any output in stdout
			BufferedReader brCleanUp = 
				new BufferedReader (new InputStreamReader(stdout));
			for(int i = 0; i < 7; i++)
				line = brCleanUp.readLine ();
			String[] tokens = line.split(",");
			tokens = tokens[1].split("\"");
			value = Double.valueOf(tokens[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
	}

}
