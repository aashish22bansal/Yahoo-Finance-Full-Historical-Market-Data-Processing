def run_wordcount(input_path, output_path):
    cmd = [
        "hadoop", "jar",
        "../../hadoop-apps/wordcount/target/wordcount.jar",
        "com.example.WordCount",
        input_path,
        output_path
    ]
    subprocess.run(cmd)
