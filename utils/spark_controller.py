import subprocess

def run_spark_job(job_name):
    cmd = [
        "spark-submit", 
        f"../../spark/etl/{job_name}.py"
    ]
    subprocess.run(cmd)
