import 'package:flutter/material.dart';

class EmptyPage extends StatelessWidget {

  const EmptyPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Empty Page"),
      ),
      body: const Center(
        child: Text("Empty content"),
      ),
    );
  }
  
}