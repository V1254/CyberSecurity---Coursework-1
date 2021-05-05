CO3099/7099 Assignment 1
========================

Score
------

* Task 1 - 50/50
* Task 2 - 45/45
* Readability - 5/5
* **Total: 100/100**




Task 1 (50 Marks)
------

You are part of a group of underground activists who want to bring down the government and unleash anarchy on the world. As part of your grand plan, you are going to develop a prototype of a ransomware program that encrypts files on others' computers and asks for money. Since it is only a prototype, it won't do everything that a normal ransomware would do. Also, someone else from your group has already written the decryption program (see below) that can be used to decrypt the files when the ransom is paid. So your encryption program should work in a "matching" way. Specifically, your program should do the following:

*   The program must be in Java and must be in a file called `WannaCry.java`.
  
*   Here is the public key of a 4096-bit master RSA keypair:
    
    MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAidRWp1iB0s2LKnAyAxUKCMmsKK9bMTdRUItZ
    VRcV4lB0RXGla0wRTNeR6r5oqNo6poHUJ+QGPjAHDCzt/MjAZdtuMSQ+Lohn+TjDMIEi2sUNeXhZuXch
    w/EE+3QTgPpIOGhjJtv4wmTjXD5UaZbYWuydNpgvFEDsF4jf02xM8t8a7nOgQIriPi83f/a4XHXcoCcG
    EHDbpbtYUhVq12rJEBXUoVM1zi9LcDhEsgil/pzRPlkT6zC+89SkgYHWTRtO2shLpJcnThkR1nyLqHU2
    Zgn1hSrNsy+T97bNL1Umhcs7/e94WJ7WWO6PoSs/t4cknPIZhhRbeBHoJ9rdV+XLBoew7buDQSht2Jn/
    zAm6A6Pvi+XhLVRlIEMLOsG6Y92Lwhuc21oS/Keqklv9yDfMznJm0aeCbm3TWZehAfPD9EKJ4LgvSVbT
    tXSiOVvPS8JtzIedISqioSvPPP5v4qqdbqobGBv2uE0sdwYhXh+dTIFSO4WG+dQHMZpdZu38l/FBec3y
    EuZJuK/pvtX5AvdYgCEwMioZxE3ph4X3S/JEbcqfR1KuuGnYwg6nmSEwotDVg55pEtSsgu3j2KRgM8GA
    7lkageikM4D6m/q6vQ5fkedfzz8PuvQn/Ne8BH3h2UZYmRjNvfKd8wt2bRKKFK7K4jCYT5riYo+5aEWS
    SrWvL+ECAwEAAQ==
    
    (The linebreaks are only added for clarity; it should be one very long string.) It is the Base64 encoding of the output of the `getEncoded()` method of a Java RSA public key. (There is, of course, also a matching private key, but you don't need to use it here.) Your program should construct a public key from this Base64 result (you should include the above string in your source code directly). This can be done using the `X509EncodedKeySpec` and `KeyFactory` classes.
  
*   The program then generates a fresh pair of random 512-bit RSA keys. **This is another pair of RSA keys, unrelated to the master RSA public key above.** The fresh RSA private key is encrypted with the master RSA public key, in ECB mode with PKCS1Padding. The result is written to a file `wannacry.key`.
  
*   Since this is only a prototype, it only encrypts one file called `test.txt`. A fresh 256-bit AES key is randomly generated. The contents of the file `test.txt` is encrypted with this AES key, in CBC mode and PKCS5Padding. The IV used is 16 empty bytes (i.e. all bits being zero). The encrypted contents is stored in a file named `test.txt.cry`, and the original file is deleted.
  
*   Finally, the AES key is encrypted with the fresh RSA public key, and the result is stored in a file `aes.key`.
  
*   All the key files, txt files, etc are in the same folder where the program runs from. You must not create them or read them from other folders.

In summary, your program will involve the use of two pairs of RSA keys and one AES key. If you wonder why it needs so many different keys, [this article](https://medium.com/@tarcisioma/ransomware-encryption-techniques-696531d07bb9) may help.

The Java program [WannaDecrypt.java](WannaDecrypt.java) is the source code of the decryption program. Unfortunately it is incomplete (that's all the guy who wrote it managed to send you before he got arrested), although you do have the compiled version in this file: [WannaDecrypt.class](WannaDecrypt.class). You should write your program in such a way that works with this decryption program (it will be used to check whether your encrypt program is correct). In particular you should be careful about how results such as encrypted keys are read from/written to files.

Task 2 (45 marks)
------

As another part of your plan, you want your group members to be able to leave messages on a server that will show them to the public. You are building your own server since you know Mr. Z will shut you down. You want the senders of these messages to be authenticated, but there is no need of confidentiality about the contents of the messages.

For some reason, you lot decided not to use the Java signature API, but rather implement something from scratch (which is a really [bad](https://security.stackexchange.com/questions/18197/why-shouldnt-we-roll-our-own) [idea](https://security.stackexchange.com/questions/209652/why-is-it-wrong-to-implement-myself-a-known-published-widely-believed-to-be)) where some BigIntegers from RSA keypairs are extracted and used to compute your own signatures. In more detail:

*   The system consists of a client and a server Java program, and they must be named `Client.java` and `Server.java` respectively. They are started by running the commands
    
    java Server port
    java Client host port userid
    
    specifying the hostname and port number of the server, and the userid of the client.
  
*   The server program is always running once started, and listens for incoming connections at the port specified. When a client is connected, the server handles the request, then waits for the next request (i.e., the server never terminates). For simplicity, you can assume that only one client will connect to the server at any one time.
  
*   Each user has a unique userid, which is a simple string like alice, bob etc. Each user is associated with a pair of RSA public and private keys, with filenames that have .pub or .prv after the userid, respectively. Thus the key files are named `alice.pub`, `bob.prv`, etc. These keys are generated separately by a program `[RSAKeyGen.java](RSAKeyGen.java)`. More details are in the comment of that program. It is assumed that the server already has the public keys of all legitimate users, via some offline method not described here, prior to the execution of the programs. All necessary keys (and only the necessary ones) are in the same folder where the client/server runs from.
  
*   The client program prompts the user to enter a message. It computes the SHA-256 digest of this message, and interpret the resulting byte array as a BigInteger x. (To avoid issues with two's-complement, in other words the byte array being interpreted as a negative number, you probably should use the `BigInteger(int, byte[])` sign-magnitude constructor of the BigInteger class.) The program extracts the RSA modulus n and private exponent d from the user's private key. It computes another BigInteger y = x^d mod n. This y is the signature. It sends the userid, the message, and the BigInteger y to the server.
  
*   Upon connecting a new client, the server receives the userid, the message and a BigInteger y from the client. It then reads the user's public key, extract the modulus n and public exponent e. It computes another BigInteger x = y^e mod n. Separately, it also computes the message digest of the received message, and interpret the resulting byte array as a BigInteger z. If x and z are equal, the signature is considered to be verified; it should print the userid and the message to the screen. If it is not verified, or if the userid is unrecognised (no corresponding key of that userid is present in the server), it should print the userid followed by a simple message stating that fact. Your server should continue to work (receive other clients) after this failed signature; in other words it should not just crash or terminate.
  
*   An example of the server output may look like this (you do not have to follow the format exactly):
    
    alice: This is an open letter to Melvin. You stand for everything I hated during that time. I'm making this as painful as I can for you.
    alice: YnBwa286Ly92dnYubnRzc2NwLmxqaC9uL3Z3Z2dvcG50dHBhdHBvL2xqaGh0aXBvL2c2amhueS93aV9qa3RpX2d0cHB0bl9wal9odGd1Y2lfbHdrY3B3Z19saWFsX2Fqamh0bm9fd2lz
    bob: I almost cried reading this. 
    carol: IM ALL IN TOMORROW. This is personal. The same children they robbed 13 years ago grew up and we have money now ourselves
    carol: Hold the line!
    melvin: \[signature not verified\]
    WSBChairman: The line was held.

