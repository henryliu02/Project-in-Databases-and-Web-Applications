import sys

def calculate_average_times(*logfiles):
    jdbc_times = []
    servlet_times = []

    for logfile in logfiles:
        with open(logfile, 'r') as f:
            lines = f.readlines()

        for line in lines:
            if "JDBC Time:" in line:
                jdbc_times.append(int(line.replace("JDBC Time: ", ""))/ 
1_000_000.0)
            elif "Servlet Time:" in line:
                servlet_times.append(int(line.replace("Servlet Time: ", ""))/ 
1_000_000.0)

    if len(jdbc_times) > 0:
        average_jdbc_time = sum(jdbc_times) / len(jdbc_times)
        print(f'Average JDBC Time: {average_jdbc_time} ms')
    else:
        print('No JDBC times were found.')

    if len(servlet_times) > 0:
        average_servlet_time = sum(servlet_times) / len(servlet_times)
        print(f'Average Servlet Time: {average_servlet_time} ms')
    else:
        print('No Servlet times were found.')

# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    calculate_average_times(*sys.argv[1:])  # Skip the first argument which is the script name


