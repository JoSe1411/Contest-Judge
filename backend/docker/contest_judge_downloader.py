import os
import json
import boto3
import subprocess
import traceback
from datetime import datetime


def log_message(message, level="INFO"):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"[{timestamp}] [{level.upper()}] {message}")


def download_files(user_id, question_id, language):
    
    try:
        log_message("=== DOWNLOADING FILES ===")
        
        if not user_id or not question_id or not language:
            raise ValueError(
                "Missing required environment variables: USER_ID, QUESTION_ID, or LANGUAGE")

        s3_client = boto3.client("s3")
        extension = get_file_extension(language)
        s3_key = os.getenv("S3_KEY")

        log_message(f"Downloading user code from S3: {s3_key}")
        local_code_path = f"/home/judgeuser/workspace/code/{user_id}/{question_id}"
        os.makedirs(local_code_path, exist_ok=True)

        local_file_path = os.path.join(local_code_path, f"Main.{extension}")
        s3_client.download_file(
            Bucket=os.getenv("AWS_S3_BUCKET_NAME"),
            Key=s3_key,
            Filename=local_file_path
        )
        log_message("User code downloaded successfully")

        
        test_files = ["input.txt", "output.txt"]
        local_test_dir = "/home/judgeuser/workspace/test"
        os.makedirs(local_test_dir, exist_ok=True)

        for file_name in test_files:
            s3_key = f"Questions/{question_id}/{file_name}"
            local_test_path = os.path.join(local_test_dir, file_name)
            try:
                s3_client.download_file(
                    Bucket=os.getenv("AWS_S3_BUCKET_NAME"),
                    Key=s3_key,
                    Filename=local_test_path,
                )
                log_message(f"Test file downloaded: {file_name}")
            except Exception as e:
                log_message(f"Failed to download test file {file_name}: {e}")
               

        return True

    except Exception as e:
        log_message(f"Download failed: {e}")
        log_message("Full stack trace:")
        traceback.print_exc()
        raise


def get_file_extension(language):
    
    extension_map = {
        "c++": "cpp",
        "cpp": "cpp",
        "python": "py",
        "py": "py",
        "java": "java",
    }
    return extension_map.get(language.lower(), "txt")


def execute_code(language, user_id, question_id):
    
    extension = get_file_extension(language)

    if extension == "cpp":
        log_message("Executing C++ code")
        return execute_cpp_code(user_id, question_id)
    elif extension == "java":
        log_message("Executing Java code")
        return execute_java_code(user_id, question_id)
    elif extension == "py":
        log_message("Executing Python code")
        return execute_py_code(user_id, question_id)
    else:
        log_message(f"Unsupported language: {language}")
        raise Exception(f"Unsupported language: {language}")


def execute_cpp_code(user_id, question_id):
    
    local_code_path = f"/home/judgeuser/workspace/code/{user_id}/{question_id}"

    log_message("Compiling C++ code")
    compile_result = subprocess.run(
        ["g++", "Main.cpp", "-o", "Main.out"],
        cwd=local_code_path,
        capture_output=True,
        text=True,
    )

    if compile_result.returncode != 0:
        log_message("C++ compilation failed")
        return {
            "exit_code": compile_result.returncode,
            "output": "",
            "expected": "",
            "passed": False,
            "error": compile_result.stderr,
        }

    log_message("C++ compilation successful")

   
    try:
        with open("/home/judgeuser/workspace/test/input.txt", "r") as f:
            input_content = f.read()
    except FileNotFoundError:
        input_content = ""

    log_message("Running C++ program")
    run_result = subprocess.run(
        ["./Main.out"],
        cwd=local_code_path,
        input=input_content,
        capture_output=True,
        text=True,
    )

   
    try:
        with open(f"{local_code_path}/output.txt", "r") as f:
            actual_output = f.read().strip()
    except FileNotFoundError:
        actual_output = run_result.stdout.strip()

    
    try:
        with open("/home/judgeuser/workspace/test/output.txt", "r") as f:
            expected_output = f.read().strip()
    except FileNotFoundError:
        expected_output = ""

    passed = actual_output == expected_output
    log_message(f"C++ execution completed - Passed: {passed}")

    return {
        "exit_code": run_result.returncode,
        "output": actual_output,
        "expected": expected_output,
        "passed": passed,
        "error": run_result.stderr,
    }


def execute_py_code(user_id, question_id):
    
    local_code_path = f"/home/judgeuser/workspace/code/{user_id}/{question_id}"

    try:
        log_message("Running Python code")
        run_result = subprocess.run(
            ["python3", "Main.py"],
            cwd=local_code_path,
            capture_output=True,
            text=True,
        )

        
        actual_output = run_result.stdout.strip()

        try:
            with open("/home/judgeuser/workspace/test/output.txt", "r") as f:
                expected_output = f.read().strip()
        except FileNotFoundError:
            expected_output = ""

        passed = actual_output == expected_output
        log_message(f"Python execution completed - Passed: {passed}")

        return {
            "exit_code": run_result.returncode,
            "output": actual_output,
            "expected": expected_output,
            "passed": passed,
            "error": run_result.stderr,
        }

    except Exception as e:
        log_message(f"Python code execution failed: {e}")
        log_message("Full stack trace:")
        traceback.print_exc()
        return {
            "exit_code": 1,
            "output": "",
            "expected": "",
            "passed": False,
            "error": str(e),
        }


def execute_java_code(user_id, question_id):

    local_code_path = f"/home/judgeuser/workspace/code/{user_id}/{question_id}"
    source_file = "Main.java"
    class_name = os.path.splitext(source_file)[0]

    log_message("Compiling Java code")
    compile_result = subprocess.run(
        ["javac", source_file],
        cwd=local_code_path,
        capture_output=True,
        text=True,
    )

    if compile_result.returncode != 0:
        log_message("Java compilation failed")
        return {
            "exit_code": compile_result.returncode,
            "output": "",
            "expected": "",
            "passed": False,
            "error": compile_result.stderr,
        }

    log_message("Java compilation successful")

    try:
        with open("/home/judgeuser/workspace/test/input.txt", "r") as f:
            input_content = f.read()
    except FileNotFoundError:
        input_content = ""

    log_message("Running Java program")
    run_result = subprocess.run(
        ["java", "-cp", ".", class_name],
        cwd=local_code_path,
        input=input_content,
        capture_output=True,
        text=True,
    )

    try:
        with open(f"{local_code_path}/output.txt", "r") as f:
            actual_output = f.read().strip()
    except FileNotFoundError:
        actual_output = run_result.stdout.strip()

    try:
        with open("/home/judgeuser/workspace/test/output.txt", "r") as f:
            expected_output = f.read().strip()
    except FileNotFoundError:
        expected_output = ""

    passed = actual_output == expected_output
    log_message(f"Java execution completed - Passed: {passed}")

    return {
        "exit_code": run_result.returncode,
        "output": actual_output,
        "expected": expected_output,
        "passed": passed,
        "error": run_result.stderr,
    }


def main():
    
    try:
        log_message("=== JUDGE SYSTEM STARTING ===")

        user_id = os.getenv("USER_ID")
        question_id = os.getenv("QUESTION_ID")
        language = os.getenv("LANGUAGE")

        log_message(f"Environment - USER_ID: {user_id}")
        log_message(f"Environment - QUESTION_ID: {question_id}")
        log_message(f"Environment - LANGUAGE: {language}")

        if not user_id or not question_id or not language:
            raise ValueError("Missing required environment variables")

        download_files(user_id, question_id, language)

        result = execute_code(language, user_id, question_id) 
        try:
            import subprocess
            log_message("=== DEBUG: CHECKING RUNNING CONTAINERS ===")
            container_check = subprocess.run(
                ["docker", "ps", "-a", "--format", "table {{.Names}}\t{{.Status}}\t{{.Ports}}"],
                capture_output=True,
                text=True,
                timeout=10
            )
            if container_check.returncode == 0:
                log_message("Running containers:")
                log_message(container_check.stdout)
            else:
                log_message(f"Docker ps failed: {container_check.stderr}")
        except Exception as e:
            log_message(f"Failed to check containers: {e}")

        
        
        print(json.dumps(result))
        log_message("=== JUDGE SYSTEM COMPLETED ===")

        return result


    except Exception as e:
        log_message(f"Judge system failed: {e}")
        error_result = {
            "exit_code": 1,
            "output": "",
            "expected": "",
            "passed": False,
            "error": str(e)
        }
        print(json.dumps(error_result))
        return error_result


if __name__ == "__main__":
    main()
